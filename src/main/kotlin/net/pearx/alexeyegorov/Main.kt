@file:JvmName("Main")

package net.pearx.alexeyegorov

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.apache.commons.compress.compressors.CompressorStreamFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URL
import java.nio.file.Files
import javax.xml.stream.XMLInputFactory
import kotlin.random.Random

val log: Logger = LoggerFactory.getLogger("alexey-egorov")
lateinit var client: JDA private set
lateinit var nouns: List<Noun>
lateinit var adjectives: List<Adjective>

fun main() {
    val tokenType = System.getenv(TOKEN_TYPE_VARIABLE)
    val token = System.getenv(TOKEN_VARIABLE)
    if(tokenType == null)
        exit("Please set the $TOKEN_TYPE_VARIABLE environment variable!")
    if(token == null)
        exit("Please set the $TOKEN_VARIABLE environment variable!")

    log.info("Loading the used word list...")
    val usedWords = WordForm::class.java.getResourceAsStream("/used.txt").reader().readLines()
    log.info("Done loading the used word list")

    if (!Files.exists(DICTIONARY_PATH)) {
        log.info("Downloading and extracting the OpenCorpora dictionary, please wait...")
        CompressorStreamFactory().createCompressorInputStream("BZIP2", URL(DICTIONARY_URL).openStream().buffered()).use {
            Files.copy(it, DICTIONARY_PATH)
        }
    }

    log.info("Processing the OpenCorpora dictionary...")
    val nounList = mutableListOf<Noun>()
    val adjectiveList = mutableListOf<Adjective>()
    Files.newInputStream(DICTIONARY_PATH).buffered().use { file ->
        val xml = XMLInputFactory.newInstance().createXMLStreamReader(file)
        val mapper = XmlMapper().apply {
            registerKotlinModule()
        }
        while (xml.skipToElement("lemma")) {
            val lemma = mapper.readValue<Lemma>(xml, Lemma::class.java)

            if (lemma.l.t !in usedWords)
                continue

            if (lemma.l.g.any { it.v in NOUN_TAGS }) {
                outer@ for (wordForm in WordForm.values()) {
                    for (g in lemma.l.g) {
                        if (wordForm.nounTag == g.v) {
                            nounList.add(Noun(lemma.l.t, wordForm))
                            break@outer
                        }
                    }
                }

            }
            if (lemma.l.g.any { it.v in ADJECTIVE_TAGS }) {
                val map = hashMapOf<WordForm, String>()
                for (f in lemma.f) {
                    if (f.g != null) {
                        if (f.g.any { it.v == NOMINATIVE_TAG }) {
                            for (wordForm in WordForm.values()) {
                                for (g in f.g) {
                                    if (wordForm.adjTag == g.v) {
                                        map[wordForm] = f.t
                                    }
                                }
                            }
                        }
                    }
                }
                if (map.size == WordForm.values().size) {
                    adjectiveList.add(Adjective(map))
                }
                else
                    log.warn("Invalid adjective: $map")
            }
        }
    }
    nouns = nounList
    adjectives = adjectiveList
    log.info("Done processing the OpenCorpora dictionary")

    log.info("Starting the Discord client...")
    client = JDABuilder(AccountType.valueOf(tokenType.toUpperCase())).apply {
        setToken(token)
        addEventListener(object : ListenerAdapter() {
            override fun onMessageReceived(e: MessageReceivedEvent) {
                if (e.author.id != client.selfUser.id) {
                    val msg = e.message.contentDisplay

                    REGEX_IF_WHAT.find(msg)?.let {
                        e.channel.sendMessage("Если что${it.groupValues[1]}?").queue()
                    }

                    if (BTW_TRIGGERS.any { it in msg.toLowerCase() } || REGEX_WHO_IS.matches(msg)) {
                        val noun = nouns[Random.nextInt(nouns.size)]
                        val adj = adjectives[Random.nextInt(adjectives.size)]
                        e.channel.sendMessage("${adj.values[noun.wordForm]!!.capitalize()} ${noun.value}").queue()
                    }
                }
            }
        })
    }.build()
    log.info("Alexey Egorov has been initialized!")
}
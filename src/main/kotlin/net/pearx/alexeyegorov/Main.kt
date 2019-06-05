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
import java.nio.file.Paths
import javax.xml.stream.XMLInputFactory
import kotlin.random.Random

val log: Logger = LoggerFactory.getLogger("alexey-egorov")
lateinit var client: JDA private set
val nouns = mutableListOf<Noun>()
val adjectives = mutableListOf<Adjective>()

fun main(vararg args: String) {
    if (args.size < 2)
        exit("Usage: alexey-egorov <bot|client> <token>")

    if (!Files.exists(DICTIONARY_PATH)) {
        log.info("Downloading and extracting the dictionary, please wait...")
        CompressorStreamFactory().createCompressorInputStream("BZIP2", URL(DICTIONARY_URL).openStream().buffered()).use {
            Files.copy(it, DICTIONARY_PATH)
        }
    }

    log.info("Processing the dictionary...")
    Files.newInputStream(DICTIONARY_PATH).buffered().use { file ->
        val xml = XMLInputFactory.newInstance().createXMLStreamReader(file)
        val mapper = XmlMapper().apply {
            registerKotlinModule()
        }
        while (xml.skipToElement("lemma")) {
            val lemma = mapper.readValue<Lemma>(xml, Lemma::class.java)

            if(lemma.l.g.any { it.v in BLOCKED_TAGS })
                continue

            if (lemma.l.g.any { it.v == "NOUN" }) {
                outer@ for (wordForm in WordForm.values()) {
                    for (g in lemma.l.g) {
                        if (wordForm.nounTag == g.v) {
                            nouns.add(Noun(lemma.l.t, wordForm))
                            break@outer
                        }
                    }
                }

            }
            if (lemma.l.g.any { it.v == "ADJF" }) {
                val map = hashMapOf<WordForm, String>()
                for (f in lemma.f) {
                    if (f.g != null) {
                        if (f.g.any { it.v == "nomn" }) {
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
                    adjectives.add(Adjective(map))
                }
                else
                    log.warn("Invalid adjective: $map")
            }
        }
        log.info("It did it!")
    }

    client = JDABuilder(AccountType.valueOf(args[0].toUpperCase())).apply {
        setToken(args[1])
        addEventListener(object : ListenerAdapter() {
            override fun onMessageReceived(e: MessageReceivedEvent) {
                if (e.author.id != client.selfUser.id) {
                    val msg = e.message.contentDisplay

                    REGEX_IF_WHAT.find(msg)?.let {
                        e.channel.sendMessage("Если что${it.groupValues[1]}?").queue()
                    }

                    if (msg.toLowerCase().contains("кстати")) {
                        val noun = nouns[Random.nextInt(nouns.size)]
                        val adj = adjectives[Random.nextInt(adjectives.size)]
                        e.channel.sendMessage("${adj.values[noun.wordForm]!!.capitalize()} ${noun.value}").queue()
                    }
                }
            }
        })
    }.build()
}
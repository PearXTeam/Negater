package net.pearx.alexeyegorov

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

enum class WordForm(val nounTag: String, val adjTag: String?) {
    MASCULINE("masc", "masc"),
    FEMININE("femn", "femn"),
    NEUTER("neut", "neut"),
    PLURAL("Pltm", "plur"),
    MS_F("ms-f", "masc")
}

data class Noun(val value: String, val wordForm: WordForm)
inline class Adjective(val values: Map<WordForm, String>)

data class Lemma(
    @JacksonXmlProperty(isAttribute = true)
    val id: String,

    @JacksonXmlProperty(isAttribute = true)
    val rev: String,

    val l: L,

    @JacksonXmlElementWrapper(useWrapping = false)
    val f: List<F>
)

data class L(
    @JacksonXmlProperty(isAttribute = true)
    val t: String,

    @JacksonXmlElementWrapper(useWrapping = false)
    val g: List<G>
)

data class F(
    val t: String,

    @JacksonXmlElementWrapper(useWrapping = false)
    val g: List<G>?
)
data class G(
    val v: String
)
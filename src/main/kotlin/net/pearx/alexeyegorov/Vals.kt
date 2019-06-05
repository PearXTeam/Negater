package net.pearx.alexeyegorov

import java.nio.file.Paths

const val DICTIONARY_URL = "http://opencorpora.org/files/export/dict/dict.opcorpora.xml.bz2"
val REGEX_IF_WHAT = Regex("если что(?:-?то)?(.*?)(?:[?\\n,.$]|$)", RegexOption.IGNORE_CASE)
val DICTIONARY_PATH = Paths.get("dict.xml")
val BLOCKED_TAGS = arrayOf("Surn", "Patr", "Name")
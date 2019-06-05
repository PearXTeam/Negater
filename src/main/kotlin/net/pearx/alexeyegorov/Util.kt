package net.pearx.alexeyegorov

import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamReader
import kotlin.system.exitProcess

fun exit(message: String): Nothing {
    log.error(message)
    exitProcess(-1)
}

fun XMLStreamReader.skipToElement(name: String): Boolean {
    while (!(eventType == XMLStreamConstants.START_ELEMENT && localName == name)) {
        if(!hasNext())
            return false
        next()
    }
    return true
}
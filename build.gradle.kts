
plugins {
    id("com.github.johnrengelman.shadow") version "5.0.0"
    kotlin("jvm") version "1.3.31"
}

group = "net.pearx.alexeyegorov.discord"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("net.dv8tion:JDA:3.8.3_463")
    implementation("ch.qos.logback:logback-classic:1.3.0-alpha4")
    implementation("org.apache.commons:commons-compress:1.18")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.9")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    named<Jar>("jar") {
        manifest {
            attributes("Main-Class" to "net.pearx.alexeyegorov.Main")
        }
    }
}
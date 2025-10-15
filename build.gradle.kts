import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.daoge.daogelab"
description = "A DgLab plugin for Minecraft: Bedrock Edition running in Allay platform"
version = "0.2.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly(group = "org.allaymc.allay", name = "api", version = "0.14.0")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")

    implementation(group = "io.netty", name = "netty-codec-http", version = "4.1.97.Final")
    implementation(group = "com.google.zxing", name = "core", version = "3.5.3")
    implementation(group = "org.apache.commons", name = "commons-lang3", version = "3.19.0")
    implementation(group = "eu.okaeri", name = "okaeri-configs-yaml-snakeyaml", version = "5.0.13")

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

tasks.shadowJar {
    archiveClassifier = "shaded"
}

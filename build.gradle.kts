import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.daoge.daogelab"
description = "Java plugin template for allay server"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    compileOnly(group = "org.allaymc.allay", name = "api", version = "861f82d6fd")
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")

    implementation(group = "io.netty", name = "netty-codec-http", version = "4.1.97.Final")
    implementation(group = "com.google.zxing", name = "core", version = "3.5.3")

    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

tasks.shadowJar {
    archiveClassifier = "shaded"
}

tasks.register<JavaExec>("runServer") {
    outputs.upToDateWhen { false }
    dependsOn("shadowJar")

    val shadowJar = tasks.named("shadowJar", ShadowJar::class).get()
    val pluginJar = shadowJar.archiveFile.get().asFile
    val cwd = layout.buildDirectory.file("run").get().asFile
    val pluginsDir = cwd.resolve("plugins").apply { mkdirs() }
    doFirst { pluginJar.copyTo(File(pluginsDir, pluginJar.name), overwrite = true) }

    val group = "org.allaymc.allay"
    val allays = configurations.compileOnly.get().dependencies.filter { it.group == group }
    val dependency = allays.find { it.name == "server" } ?: allays.find { it.name == "api" }!!
    val server = dependencies.create("$group:server:${dependency.version}")
    classpath = files(configurations.detachedConfiguration(server).resolve())
    mainClass = "org.allaymc.server.Allay"
    workingDir = cwd
}

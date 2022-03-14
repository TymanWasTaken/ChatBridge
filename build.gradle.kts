import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "tech.tyman.chatbridge"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://papermc.io/repo/repository/maven-public/")
}

val include: Configuration by configurations.creating
project.configurations.implementation.get().extendsFrom(include)

dependencies {
    compileOnly("io.papermc.paper", "paper-api", "1.18.2-R0.1-SNAPSHOT")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.0")
    include("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("dev.kord:kord-core:0.8.0-M12")
    implementation("com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:1.6.0")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    configurations = listOf(include)
    relocate("co.aikar.commands", "tech.tyman.chatbridge.acf")
    relocate("co.aikar.locales", "tech.tyman.chatbridge.locales")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.javaParameters = true
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
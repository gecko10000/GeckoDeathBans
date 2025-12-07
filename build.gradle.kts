plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "1.4.20"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("de.eldoria.plugin-yml.bukkit") version "0.6.0"
    kotlin("kapt") version "2.2.0"
}

sourceSets {
    main {
        java {
            srcDir("src")
        }
        resources {
            srcDir("res")
        }
    }
}

group = "gecko10000.geckodeathbans"
version = "0.1"

bukkit {
    name = "GeckoDeathBans"
    main = "$group.$name"
    apiVersion = "1.13"
    depend = listOf("GeckoLib", "LibertyBans", "CMI")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
    maven("https://jitpack.io")

    maven("https://mvn-repo.arim.space/lesser-gpl3/")
    maven("https://mvn-repo.arim.space/gpl3/")
    maven("https://mvn-repo.arim.space/affero-gpl3/")
    mavenLocal()
}

dependencies {
    compileOnly(kotlin("stdlib", version = "2.2.0"))
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")
    compileOnly("gecko10000.geckolib:GeckoLib:1.1")
    compileOnly("net.strokkur", "commands-annotations", "1.5.0")
    kapt("net.strokkur", "commands-processor", "1.5.0")

    compileOnly("space.arim.libertybans:bans-api:1.1.2")
    compileOnly("com.github.Zrips:CMI-API:9.7.14.3")
}

kotlin {
    jvmToolchain(21)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}


tasks.register("update") {
    dependsOn(tasks.build)
    doLast {
        exec {
            workingDir(".")
            commandLine("../../dot/local/bin/update.sh")
        }
    }
}

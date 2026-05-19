pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
        maven("https://snapshots-repo.kordex.dev")
        maven("https://releases-repo.kordex.dev")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.4"
    id("dev.kikugie.loom-back-compat") version "0.3"
}

stonecutter {
    create(rootProject) {
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        versions("1.20.1", "1.21.1", "1.21.11", "26.1.2")
        version("1.20.1-krpc", "1.20.1")
        version("1.20.1-full", "1.20.1")
        version("1.21.1-krpc", "1.21.1")
        version("1.21.1-full", "1.21.1")
        version("1.21.11-krpc", "1.21.11")
        version("1.21.11-full", "1.21.11")
        version("26.1.2-krpc", "26.1.2")
        version("26.1.2-full", "26.1.2")
        vcsVersion = "1.21.1-full"
    }
}

rootProject.name = "Khat"
include("kamera")
include("kamera-client")
include("krapher")
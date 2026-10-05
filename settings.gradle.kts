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
        fun createVersions(vararg versions: String) {
            this.versions(versions.toList())
            for (v in versions) {
                version("$v-krpc", v)
                version("$v-full", v)
            }
        }
        // See https://stonecutter.kikugie.dev/wiki/start/#choosing-minecraft-versions
        createVersions("1.20.1", "1.21.1", "1.21.11", "26.1.2", "26.3")
        vcsVersion = "26.3-full"
    }
}

rootProject.name = "Khat"
include("kamera")
include("kamera-client")
include("krapher")
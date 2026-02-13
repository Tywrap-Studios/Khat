plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.2.20"

    id("com.google.devtools.ksp") version "2.2.20-2.0.3"
    id("io.ktor.plugin") version "3.3.2"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.1"
    id("dev.kordex.gradle.i18n") version "1.1.1"
}

group = "org.tywrapstudios"
version = "0.1.0"

val requiredJava = JavaVersion.VERSION_17

repositories {
    mavenCentral()

    maven("https://snapshots-repo.kordex.dev") { name = "KordEx (Snapshots, R2)" }
    maven("https://releases-repo.kordex.dev") { name = "KordEx (Releases, R2)" }
    maven("https://repo.kordex.dev/snapshots") { name = "KordEx (Snapshots + Mirror, Reposilite)" }
    maven("https://mirror-repo.kordex.dev") { name = "KordEx (Mirror, R2)" }
}

dependencies {
    implementation("dev.kordex:kord-extensions:${property("deps.kordex")}")
    ksp("dev.kordex:annotation-processor:${property("deps.kordex")}")

    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("io.ktor:ktor-client-auth")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:${property("deps.krpc")}")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:${property("deps.krpc")}")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:${property("deps.krpc")}")
    implementation(project(":kamera"))
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

kotlin {
    jvmToolchain(requiredJava.majorVersion.toInt())
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(requiredJava.majorVersion))
    }
}

i18n {
    bundle("krapher.strings", "org.tywrapstudios.krapher.i18n")
}
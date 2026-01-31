plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("io.ktor.plugin") version "3.3.2"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.1"
}

group = "org.tywrapstudios"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:${property("deps.krpc")}")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:${property("deps.krpc")}")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:${property("deps.krpc")}")
    implementation(project(":kamera"))
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(JavaVersion.VERSION_21.majorVersion.toInt())
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(JavaVersion.VERSION_21.majorVersion))
    }
}
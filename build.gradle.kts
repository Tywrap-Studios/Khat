plugins {
    id("net.fabricmc.fabric-loom-remap")
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.1"
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

val variant = sc.current.project.substringAfter("-", "")

val addition = if (!variant.isEmpty()) "-$variant" else ""
val mcVersion = sc.current.version

val archivesVersion = "${property("mod.version")}+$mcVersion$addition"
version = archivesVersion
base.archivesName = property("mod.id") as String

val requiredJava = when {
    sc.current.parsed >= "1.20.6" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

val krpc = variant == "krpc" || variant == "full"
val full = variant == "full"

repositories {
    mavenLocal()
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    maven("https://jitpack.io") { name = "JitPack" }
    maven("https://snapshots-repo.kordex.dev") { name = "KordEx (Snapshots, R2)" }
    maven("https://releases-repo.kordex.dev") { name = "KordEx (Releases, R2)" }
    maven("https://repo.kordex.dev/snapshots") { name = "KordEx (Snapshots + Mirror, Reposilite)" }
    maven("https://mirror-repo.kordex.dev") { name = "KordEx (Mirror, R2)" }
}

val includeImplementation: Configuration by configurations.creating {
    configurations.implementation.configure { extendsFrom(this@creating) }
}

configurations {
    includeInternal {
        exclude(group = "io.ktor", module = "ktor-bom")
        exclude(group = "dev.whyoleg.sweetspi", module = "sweetspi-bom")
        exclude(group = "com.fasterxml.jackson", module = "jackson-bom")
    }
}

dependencies {
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, property("deps.fabric_api") as String))
    }

//    fun DependencyHandlerScope.includeImplementation(dep: Any, condition: () -> Boolean = { true }) {
//        if (condition()){
//            implementation(dep)
//            include(dep)
//        }
//    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.fabric_kotlin")}")

    /* Libraries */
    // Config
    includeImplementation("com.uchuhimo:konf-core:${property("deps.konf")}")
    includeImplementation("com.uchuhimo:konf-toml:${property("deps.konf")}")
    // mclo.gs API
    includeImplementation("gs.mclo:api:${property("deps.mclogs")}")
    // hookt
    includeImplementation("com.github.Tywrap-Studios:hookt:${property("deps.hookt")}") {
        exclude("org.slf4j", "slf4j-simple")
    }
    // Ktor Client
    includeImplementation("io.ktor:ktor-client-core:${property("deps.ktor")}")
    includeImplementation("io.ktor:ktor-client-cio:${property("deps.ktor")}")
    includeImplementation("io.ktor:ktor-client-content-negotiation:${property("deps.ktor")}")
    includeImplementation("io.ktor:ktor-serialization-kotlinx-json:${property("deps.ktor")}")
    if (krpc) {
        // Ktor Server
        includeImplementation("io.ktor:ktor-server-cio-jvm:${property("deps.ktor")}")
        includeImplementation("io.ktor:ktor-server-auth:${property("deps.ktor")}")
        // kRPC
        includeImplementation("org.jetbrains.kotlinx:kotlinx-rpc-core:${property("deps.krpc")}")
        includeImplementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:${property("deps.krpc")}")
        includeImplementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:${property("deps.krpc")}")
        includeImplementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:${property("deps.krpc")}")
        // kamera
        includeImplementation(project(":kamera"))
        // Exposed
        includeImplementation("org.jetbrains.exposed:exposed-core:${property("deps.exposed")}")
        includeImplementation("org.jetbrains.exposed:exposed-dao:${property("deps.exposed")}")
        includeImplementation("org.jetbrains.exposed:exposed-java-time:${property("deps.exposed")}")
        includeImplementation("org.jetbrains.exposed:exposed-jdbc:${property("deps.exposed")}")
        includeImplementation("org.jetbrains.exposed:exposed-json:${property("deps.exposed")}")
        includeImplementation("org.xerial:sqlite-jdbc:${property("deps.sqlite-jdbc")}")
    }
    if (full) {
        // krapher
        includeImplementation(project(":krapher"))
        // ICU4J
        includeImplementation("com.ibm.icu:icu4j:77.1")
    }

    /* Compat */
    // Spark
    compileOnly("maven.modrinth:spark:${property("deps.spark")}")

    fapi(
        "fabric-lifecycle-events-v1",
        "fabric-message-api-v1",
        "fabric-command-api-v2",
    )
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json") // Useful for interface injection
    accessWidenerPath = rootProject.file("src/main/resources/${property("mod.id")}.accesswidener")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1") // Adds names to lambdas - useful for mixins
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true") // Exports transformed classes for debugging
    }
    // Configure run directories to be per environment per version
    // for dependency mods and to test on a versioned server with a
    // "clean" client, uses "other" as the name if it can't detect
    // the environment.
    runConfigs.forEach {
        var runDirectory: String? = null
        if (it.environment == "client") {
            runDirectory = "./run/client"
        }
        if (it.environment == "server") {
            runDirectory = "./run/server"
        }
        if (runDirectory == null) {
            runDirectory = "other"
        }
        it.runDir = runDirectory
    }
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

tasks {
    processResources {
        inputs.property("id", project.property("mod.id"))
        inputs.property("name", project.property("mod.name"))
        inputs.property("version", project.property("mod.version"))
        inputs.property("minecraft", project.property("mod.mc_dep"))

        val props = mapOf(
            "id" to project.property("mod.id"),
            "name" to project.property("mod.name"),
            "version" to project.property("mod.version"),
            "minecraft" to project.property("mod.mc_dep")
        )

        filesMatching("fabric.mod.json") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }
    }

    // Builds the version into a shared folder in `build/libs/${mod version}/`
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile }, remapSourcesJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}+${project.property("mod.mc_title")}"))
        dependsOn("build")
    }
}

afterEvaluate {
    dependencies {
        handleIncludes(includeImplementation)
    }
}

/* Thanks to https://github.com/jakobkmar for original script */
fun DependencyHandlerScope.includeTransitive(
    dependencies: Set<ResolvedDependency>,
    minecraftLibs: Set<ResolvedDependency>,
    kotlinDependency: ResolvedDependency,
    checkedDependencies: MutableSet<ResolvedDependency> = HashSet()
) {
    dependencies.forEach {
        if (checkedDependencies.contains(it) ||
            it.moduleGroup == "org.jetbrains.kotlin"
            || (it.moduleGroup == "org.jetbrains.kotlinx" &&
                    (it.moduleName.startsWith("kotlinx-coroutines") || it.moduleName.startsWith("kotlinx-serialization")))
        ) return@forEach

        fun doInclusion(name: String) {
            include(name)
            println("Including -> $name")
        }

        val includeAnyway = arrayOf(
            "icu4j"
        )

        if (kotlinDependency.children.any { dep -> dep.name == it.name }) {
            println("Skipping -> ${it.name} (already in fabric-language-kotlin)")
        } else if (minecraftLibs.any { dep -> dep.moduleGroup == it.moduleGroup && dep.moduleName == it.moduleName }) {
            println("Skipping -> ${it.name} (already in minecraft (${minecraftLibs.first { dep -> dep.moduleGroup == it.moduleGroup && dep.moduleName == it.moduleName }.moduleVersion}))")
            if (includeAnyway.any { element -> it.moduleName.startsWith(element) }) {
                println("   Including Anyway!")
                doInclusion(it.name)
            }
        } else {
            doInclusion(it.name)
        }
        checkedDependencies += it

        includeTransitive(it.children, minecraftLibs, kotlinDependency, checkedDependencies)
    }
}

fun DependencyHandlerScope.handleIncludes(configuration: Configuration) {
    includeTransitive(
        configuration.resolvedConfiguration.firstLevelModuleDependencies,
        configurations.minecraftLibraries.get().resolvedConfiguration.firstLevelModuleDependencies,
        configurations.modImplementation.get().resolvedConfiguration.firstLevelModuleDependencies
            .first { it.moduleGroup == "net.fabricmc" && it.moduleName == "fabric-language-kotlin" },
    )
}

// Publishes builds to Modrinth and GitHub with changelog from the CHANGELOG.md file
publishMods {
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })
    displayName = "${property("mod.name")} ${property("mod.version")} for ${property("mod.mc_title")} (${property("mod.function_title")})"
    version = archivesVersion
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add("fabric")

    dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

    modrinth {
        projectId = property("publish.modrinth") as String
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        minecraftVersions.addAll(property("mod.mc_targets").toString().split(' '))

        requires("fabric-api", "fabric-language-kotlin")
    }
}

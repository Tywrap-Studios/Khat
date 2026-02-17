plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
}

stonecutter active "1.21.1-full"

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    val variant = node.metadata.project.substringAfter("-", "")

    val addition = if (!variant.isEmpty()) "-$variant" else ""
    val version = node.metadata.version
    val modVersion = "${property("mod.version")}+$version$addition"
    swaps["mod_version"] = "\"$modVersion\""
    swaps["minecraft"] = "\"$version\""
    swaps["mod_id"] = "\"${property("mod.id")}\""

    constants["krpc"] = variant == "krpc" || variant == "full"
    constants["full"] = variant == "full"

    replacements {
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
    }
}

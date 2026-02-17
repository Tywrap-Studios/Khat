plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT" apply false
    // id("me.modmuss50.mod-publish-plugin") version "1.0.+" apply false
}

stonecutter active "1.21.1-full"

/*
// Make newer versions be published last
stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}
 */

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

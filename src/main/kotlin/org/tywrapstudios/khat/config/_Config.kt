package org.tywrapstudios.khat.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml
import net.fabricmc.loader.api.FabricLoader
import net.tassia.diskord.webhook.Webhook
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories

private fun getConfigDirectory(): Path {
    return FabricLoader.getInstance().configDir.resolve("khat").createDirectories()
}

private fun getMainConfig(): File {
    val file = File(getConfigDirectory().toFile(), "global.toml")
    file.createNewFile()
    return file
}

private val GLOBAL_CONFIG get() = Config { addSpec(KhatSpec) }
//        .from.toml.resource("/default-configs/global.toml", false)
    .from.toml.watchFile(getMainConfig(), 5, optional = false)
    .validateRequired()

fun getGlobalConfig(): Config {
    GLOBAL_CONFIG
        .toToml
        .toFile(getMainConfig())
    return GLOBAL_CONFIG
}
val WEBHOOKS get() = getGlobalConfig()[KhatSpec.DiscordSpec.webhooks]
    .map { Webhook(it) }
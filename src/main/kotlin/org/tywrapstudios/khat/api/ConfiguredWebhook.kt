package org.tywrapstudios.khat.api

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import net.fabricmc.loader.api.FabricLoader
import org.tywrapstudios.hookt.DiscordWebhook
import org.tywrapstudios.khat.KhatMod.MOD_ID
import org.tywrapstudios.khat.config.WEBHOOK_PATH
import org.tywrapstudios.khat.config.WebhookSpec
import org.tywrapstudios.khat.config.id
import java.io.File
import java.nio.file.Files
import kotlin.io.path.createDirectories

class ConfiguredWebhook(
    val webhook: DiscordWebhook,
    config: Config? = null
) {
    val config = config ?: Config {
        addSpec(WebhookSpec)
    }
        .from.toml.resource("default-configs/webhook.toml")
        .from.toml.watchFile(generateFile())

    fun generateFile(): File {
        val configPath = FabricLoader.getInstance()
            .configDir
            .resolve(WEBHOOK_PATH)
            .resolve("${webhook.id}.toml")
        if (!Files.exists(configPath)) {
            configPath.parent.createDirectories()
            val resource = FabricLoader
                .getInstance()
                .getModContainer(MOD_ID).get()
                .findPath("default-configs/webhook.toml").get()

            return Files.copy(
                resource,
                configPath
            ).toFile()
        }
        return configPath.toFile()
    }
}

package org.tywrapstudios.khat.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import net.fabricmc.loader.api.FabricLoader
import net.tassia.diskord.webhook.Webhook

const val CONFIG_PATH = "khat/global.toml"

val globalConfig = Config {
    addSpec(KhatSpec)
    addSpec(DiscordSpec)
}
    .from.toml.resource("default-configs/global.toml")
    .from.toml.watchFile(FabricLoader.getInstance().configDir.resolve(CONFIG_PATH).toFile())
    .from.env()
    .from.systemProperties()

val webhooks get() = globalConfig[DiscordSpec.webhooks]
    .map {
        Webhook(it) {
            client = HttpClient(Java)
        }
    }
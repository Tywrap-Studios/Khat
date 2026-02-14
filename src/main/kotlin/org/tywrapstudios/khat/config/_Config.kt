package org.tywrapstudios.khat.config

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import net.fabricmc.loader.api.FabricLoader
import org.tywrapstudios.hookt.DiscordWebhook
import org.tywrapstudios.hookt.Webhook
import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.KhatMod.MOD_ID
import org.tywrapstudios.khat.api.ConfiguredWebhook
import java.nio.file.Files
import kotlin.io.path.createDirectories

const val CONFIG_PATH = "khat/"
const val GLOBAL_PATH = "${CONFIG_PATH}global.toml"
const val WEBHOOK_PATH = "${CONFIG_PATH}webhooks/"

lateinit var globalConfig: Config

val webhooks
    get() = globalConfig[KhatSpec.webhooks]
        .map {
            val webhook: ConfiguredWebhook by lazy {
                ConfiguredWebhook(Webhook(it))
            }
            webhook
        }

val DiscordWebhook.id get() = context.id.toString()
val ConfiguredWebhook.id get() = webhook.id

fun initializeConfigs() {
    val globalPath = FabricLoader.getInstance().configDir.resolve(GLOBAL_PATH)
    KhatMod.LOGGER.info("Loading config from $globalPath")
    if (!Files.exists(globalPath)) {
        globalPath.parent.createDirectories()
        val resource = FabricLoader
            .getInstance()
            .getModContainer(MOD_ID).get()
            .findPath("default-configs/global.toml").get()

        Files.copy(
            resource,
            globalPath
        )
    }

    globalConfig = Config {
        addSpec(KhatSpec)
        addSpec(RpcSpec)
        addSpec(BotSpec)
    }
        .from.toml.resource("default-configs/global.toml")
        .from.toml.watchFile(FabricLoader.getInstance().configDir.resolve(GLOBAL_PATH).toFile())
        .from.env()
        .from.systemProperties()
        .validateRequired()
}


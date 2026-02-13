package org.tywrapstudios.krapher

import dev.kordex.core.ExtensibleBot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.krapher.extensions.minecraft.CommandExtension

val logger: Logger = LoggerFactory.getLogger("Krapher")

object BotInitializer {

    lateinit var bot: ExtensibleBot
    lateinit var config: BotConfig

    private suspend fun setup(token: String) = ExtensibleBot(token) {

        hooks {
            beforeStart {
                KameraClient.connect()
            }
        }

        extensions {
            add(::CommandExtension)
        }

        intents(false) {

        }
    }

    suspend fun start(config: BotConfig) {
        bot = setup(config.token)
        this.config = config
        bot.start()
    }
}

data class BotConfig(
    val token: String,
    val mRpcToken: String,
    val mRpcPort: Int,
    val moderators: Set<ULong>
)
package org.tywrapstudios.krapher

import dev.kordex.core.ExtensibleBot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.krapher.extensions.minecraft.ChatExtension
import org.tywrapstudios.krapher.extensions.minecraft.CommandExtension
import org.tywrapstudios.krapher.extensions.minecraft.LinkingExtension

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
            if (config.featureSet.commands) add(::CommandExtension)
            if (config.featureSet.chat) add(::ChatExtension)
            if (config.featureSet.linking) add(::LinkingExtension)
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
    val chat: ULong,
    val moderators: Set<ULong>,
    val featureSet: FeatureSet,
)

data class FeatureSet(
    val chat: Boolean,
    val linking: Boolean,
    val commands: Boolean,
)
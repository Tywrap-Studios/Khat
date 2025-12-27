package org.tywrapstudios.khat

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import gs.mclo.api.MclogsClient
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.fabricmc.loader.api.FabricLoader
import net.tassia.diskord.webhook.Webhook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.config.KhatSpec
import org.tywrapstudios.khat.logic.HandleMinecraft
import java.io.File

object KhatMod : DedicatedServerModInitializer {
	val LOGGER: Logger = LoggerFactory.getLogger("Khat")
	const val VERSION: String =  /*$ mod_version*/"2.0.0"
	const val MINECRAFT: String =  /*$ minecraft*/"1.21.1"

	val MCL: MclogsClient = MclogsClient("Khat", VERSION, MINECRAFT)

    override fun onInitializeServer() {
		registerEvents()
    }

	internal fun registerEvents() {
		val console = McPlayer("Console", "console")
		ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvents.ServerStarted {
			HandleMinecraft.handleChatMessage("Server started.", console)
		})

		ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleEvents.ServerStopped {
			HandleMinecraft.handleChatMessage("Server stopped.", console)
		})

		ServerMessageEvents.CHAT_MESSAGE.register { signedMessage, serverPlayerEntity, _ ->
            val message: String = signedMessage.decoratedContent().string
            val authorUuid: String = signedMessage.sender().toString()
            val authorName: String = serverPlayerEntity.name.string
            HandleMinecraft.handleChatMessage(message, McPlayer(authorName, authorUuid))
        }

        ServerMessageEvents.GAME_MESSAGE.register { _, text, _ ->
            HandleMinecraft.handleGameMessage(text.string)
        }

        ServerMessageEvents.COMMAND_MESSAGE.register { signedMessage, serverCommandSource, _ ->
            val message: String = signedMessage.decoratedContent().string
            val authorUuid: String = signedMessage.sender().toString()
            val authorName: String = serverCommandSource.textName
            HandleMinecraft.handleChatMessage(message, McPlayer(authorName, authorUuid))
        }

//		CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
//			CTDCommand.register(dispatcher)
//		}
	}
}
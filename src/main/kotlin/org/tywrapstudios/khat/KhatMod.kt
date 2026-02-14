package org.tywrapstudios.khat

import gs.mclo.api.MclogsClient
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.kamera.api.LinkService
import org.tywrapstudios.kamera.api.ServerStatsService
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.command.CommandImpl
import org.tywrapstudios.khat.config.BotSpec
import org.tywrapstudios.khat.config.RpcSpec
import org.tywrapstudios.khat.config.globalConfig
import org.tywrapstudios.khat.config.initializeConfigs
import org.tywrapstudios.khat.config.migration.v2.MigrateV2
import org.tywrapstudios.khat.database.DatabaseManager
import org.tywrapstudios.khat.logic.HandleMinecraft
import org.tywrapstudios.khat.platform.kamera.ChatServiceImpl
import org.tywrapstudios.khat.platform.kamera.LinkServiceImpl
import org.tywrapstudios.khat.platform.kamera.ServerStatsServiceImpl
import org.tywrapstudios.khat.platform.kamera.command.CommandServiceImpl
import org.tywrapstudios.krapher.BotConfig
import org.tywrapstudios.krapher.BotInitializer
import org.tywrapstudios.krapher.FeatureSet
import kotlin.coroutines.CoroutineContext

object KhatMod : DedicatedServerModInitializer, CoroutineScope {
    val LOGGER: Logger = LoggerFactory.getLogger("Khat")
    const val VERSION: String =  /*$ mod_version*/"2.0.0"
    const val MINECRAFT: String =  /*$ minecraft*/"1.21.1"
    const val MOD_ID: String = /*$ mod_id*/"ctd"

    val MCL: MclogsClient = MclogsClient("Khat", VERSION, MINECRAFT)
    lateinit var SERVER: MinecraftServer
    var RPC_JOB: Job? = null
    var BOT_JOB: Job? = null

    override val coroutineContext: CoroutineContext = Dispatchers.Default + CoroutineName("Khat")

    override fun onInitializeServer() {
        initializeConfigs()
        MigrateV2.attemptMigrate()

        registerEvents()

        if (globalConfig[RpcSpec.enabled]) {
            startRpc()
            if (globalConfig[BotSpec.enabled]) {
                startBot()
            }
        }
    }

    private fun startRpc() {
        RPC_JOB = KhatMod.launch {
            embeddedServer(Netty, port = globalConfig[RpcSpec.port]) {
                install(Authentication) {
                    bearer("rpc-auth") {
                        if (globalConfig[RpcSpec.token].isEmpty()) {
                            throw IllegalStateException("No token provided for RPC server.")
                        }
                        authenticate { credentials ->
                            if (credentials.token == globalConfig[RpcSpec.token]) {
                                UserIdPrincipal("rpc-client")
                                LOGGER.info("RPC Client authenticated")
                            } else {
                                null
                            }
                        }
                    }
                }
                install(Krpc)

                routing {
                    authenticate("rpc-auth") {
                        rpc("/kamera") {
                            rpcConfig {
                                serialization {
                                    json()
                                }
                            }

                            registerService<ServerStatsService> { ServerStatsServiceImpl }
                            if (globalConfig[RpcSpec.FeatureSpec.linking]) {
                                registerService<LinkService> { LinkServiceImpl }
                            }
                            if (globalConfig[RpcSpec.FeatureSpec.commands]) {
                                registerService<CommandService> { CommandServiceImpl }
                            }
                            if (globalConfig[RpcSpec.FeatureSpec.chat]) {
                                registerService<ChatService> { ChatServiceImpl }
                            }
                        }
                    }
                }
                LOGGER.info("Started RPC server")
            }.start(wait = true)
        }
    }

    private fun startBot() {
        BOT_JOB = KhatMod.launch {
            BotInitializer.start(
                BotConfig(
                    globalConfig[BotSpec.token],
                    globalConfig[RpcSpec.token],
                    globalConfig[RpcSpec.port],
                    globalConfig[BotSpec.channel].toULong(),
                    globalConfig[BotSpec.moderators].map { it.toULong() }.toSet(),
                    globalConfig[BotSpec.enforceUsername],
                    globalConfig[BotSpec.pattern],
                    FeatureSet(
                        globalConfig[RpcSpec.FeatureSpec.chat],
                        globalConfig[RpcSpec.FeatureSpec.linking],
                        globalConfig[RpcSpec.FeatureSpec.commands],
                    ),
                )
            )
        }
    }

    private fun registerEvents() {
        val console = McPlayer("Console", "console")
        ServerLifecycleEvents.SERVER_STARTED.register {
            SERVER = it
            DatabaseManager.setup()
            HandleMinecraft.handleChatMessage("Server started.", console)
        }

        ServerLifecycleEvents.SERVER_STOPPED.register {
            HandleMinecraft.handleChatMessage("Server stopped.", console)
        }

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

        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            CommandImpl.register(dispatcher)
        }
    }
}
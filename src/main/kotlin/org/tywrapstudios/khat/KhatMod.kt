package org.tywrapstudios.khat

import gs.mclo.api.MclogsClient
//? if krpc {
import org.tywrapstudios.khat.config.RpcSpec
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.engine.*
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.Krpc
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import org.tywrapstudios.khat.platform.kamera.ChatServiceImpl
import org.tywrapstudios.khat.platform.kamera.LinkServiceImpl
import org.tywrapstudios.khat.platform.kamera.ServerStatsServiceImpl
import org.tywrapstudios.khat.platform.kamera.command.CommandServiceImpl
import org.tywrapstudios.kamera.api.ChatService
import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.kamera.api.LinkService
import org.tywrapstudios.kamera.api.ServerStatsService
import org.tywrapstudios.khat.database.DatabaseManager
import org.tywrapstudios.khat.config.globalConfig
//?}
//? if full {
import org.tywrapstudios.khat.config.BotSpec
import org.tywrapstudios.krapher.BotConfig
import org.tywrapstudios.krapher.BotInitializer
import org.tywrapstudios.krapher.FeatureSet
//?}
import kotlinx.coroutines.*
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.minecraft.server.MinecraftServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.api.sendLiteral
import org.tywrapstudios.khat.command.CommandImpl
import org.tywrapstudios.khat.config.WebhookSpec
import org.tywrapstudios.khat.config.initializeConfigs
import org.tywrapstudios.khat.config.migration.v2.MigrateV2
import org.tywrapstudios.khat.config.webhooks
import org.tywrapstudios.khat.logic.HandleMinecraft
import org.tywrapstudios.khat.logic.handleAll
import kotlin.coroutines.CoroutineContext

object KhatMod : DedicatedServerModInitializer, CoroutineScope {
    val LOGGER: Logger = LoggerFactory.getLogger("Khat")
    const val VERSION: String =  /*$ mod_version*/"2.1.0+26.3-full"
    const val MINECRAFT: String =  /*$ minecraft*/"26.3"
    const val MOD_ID: String = /*$ mod_id*/"ctd"

    val MCL: MclogsClient = MclogsClient("Khat", VERSION, MINECRAFT)
    lateinit var SERVER: MinecraftServer
    //? if krpc
    var RPC_JOB: Job? = null
    //? if krpc
    var RPC_SERVER: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null
    //? if full
    var BOT_JOB: Job? = null

    override val coroutineContext: CoroutineContext = Dispatchers.Default + CoroutineName("Khat")

    override fun onInitializeServer() {
        initializeConfigs()
        MigrateV2.attemptMigrate()

        registerEvents()

        //? if krpc {
        if (globalConfig[RpcSpec.enabled]) {
            startRpc()
            //? if full {
            if (globalConfig[BotSpec.enabled]) {
                startBot()
            }
            //?}
        } //? if full {
        else if (globalConfig[BotSpec.enabled]) {
            KhatMod.launch {
                webhooks.forEach {
                    var message = ("You are trying to run the bot without the kRPC server enabled." +
                            "\nEnsure you are running the kRPC server in the config.").handleAll(it.config)
                    if (!it.config[WebhookSpec.useEmbeds]) {
                        message = "**$message**"
                    }
                    it.sendLiteral(message)
                }
            }
            throw IllegalStateException("Tried to use bot without kRPC server. Ensure it's enabled in config.")
        }
        //?}
        //?}
    }

    //? if krpc {
    private fun startRpc() {
        RPC_JOB = KhatMod.launch {
            val server = embeddedServer(CIO, port = globalConfig[RpcSpec.port]) {
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
            }.also {
                RPC_SERVER = it
            }

            try {
                LOGGER.info("Starting RPC server")
                server.start(wait = true)
                awaitCancellation()
            } catch (e: CancellationException) {
                LOGGER.info("RPC Server requested cancellation. Shutting down... This can take a short minute, this is not a bug!")
                throw e
            } finally {
                LOGGER.info("Finally: RPC Server shutting down...")
                server.stop(1000, 5000)
            }
        }
    }
    //?}
    //? if full {
    private fun startBot() {
        BOT_JOB = KhatMod.launch {
            try {
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
            } catch (e: CancellationException) {
                LOGGER.info("Krapher requested cancellation. Shutting down...")
                throw e
            } finally {
                LOGGER.info("Finally: Krapher shutting down...")
            }
        }
    }
    //?}

    private fun registerEvents() {
        val console = McPlayer("Console", "console")
        ServerLifecycleEvents.SERVER_STARTED.register {
            SERVER = it
            //? if krpc
            DatabaseManager.setup()
            HandleMinecraft.handleChatMessage("Server started.", console)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            LOGGER.info("Shutting down Khat...")
            //? if full {
            BotInitializer.close()
            runBlocking { BOT_JOB?.cancelAndJoin() }
            //?}

            //? if krpc {
            runBlocking { RPC_JOB?.cancelAndJoin() }
            //?}
            LOGGER.info("Finished marking Khat jobs for shutdown")
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
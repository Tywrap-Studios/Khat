@file:OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)

package org.tywrapstudios.khat.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.uchuhimo.konf.source.toml.toToml
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toDateTimePeriod
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.commands.arguments.UuidArgument
import net.minecraft.network.chat.Component
import org.tywrapstudios.kamera.api.LinkStatus
import org.tywrapstudios.kamera.api.VerificationResult
import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.compat.handleSparkWorldTimeOut
import org.tywrapstudios.khat.config.KhatSpec
import org.tywrapstudios.khat.config.globalConfig
import org.tywrapstudios.khat.config.id
import org.tywrapstudios.khat.config.webhooks
import org.tywrapstudios.khat.logic.HandleMinecraft
import org.tywrapstudios.khat.platform.kamera.LinkServiceImpl
import java.nio.file.Files
import java.util.concurrent.TimeoutException
import kotlin.io.path.createDirectories
import kotlin.io.path.writer
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

@Suppress("unused")
object CommandExecutions {

    /*
     *  ROOT
     */

    internal fun root(context: CommandContext<CommandSourceStack>): Int {
        val source = context.getSource()
        val message = """
                    -----[Chat To Discord]-----
                    > Mod Version: ${KhatMod.VERSION}
                    > Config Version: ${globalConfig[KhatSpec.version]}
                    > Webhooks Defined: ${globalConfig[KhatSpec.webhooks].size}
                    -----------------------
                    """.trimIndent()
        source.sendSuccess({
            Component.literal(message).withStyle(ChatFormatting.BLUE)
        }, false)
        return 1
    }

    /*
     *  DEBUG
     */

    internal fun dumpConfig(context: CommandContext<CommandSourceStack>): Int {
        val source = context.getSource()
        val message = """
                    --------[Config]---------
                    ${
            webhooks.joinToString("\n") {
                """${it.id}:
                            |${it.config.toToml.toText()}
                        """.trimMargin()
            }
        }
                    -----------------------
                    """.trimIndent()
        source.sendSuccess({
            Component.literal(message).withStyle(ChatFormatting.GRAY)
        }, false)
        return 1
    }

    internal fun forceChatMessage(context: CommandContext<CommandSourceStack>): Int {
        try {
            val player = context.source.player
            HandleMinecraft.handleChatMessage("Debug!", McPlayer(player!!.name.string, player.uuid.toString()))
            return 1
        } catch (_: NullPointerException) {
            HandleMinecraft.handleChatMessage("Debug!", McPlayer("Console", "console"))
            return 1
        } catch (_: Exception) {
        }
        return 0
    }

    internal fun forceGameMessage(context: CommandContext<CommandSourceStack>): Int {
        HandleMinecraft.handleGameMessage("Debug Game Message")
        return 1
    }

    internal fun forceCrashMessage(context: CommandContext<CommandSourceStack>): Int {
        val path = FabricLoader.getInstance()
            .gameDir
            .resolve(".debug/khat/")
        path.createDirectories()
        val file = Files.createTempFile(path, "debug-crash", ".log")
        file.writer().use {
            it.write("Debug")
        }
        HandleMinecraft.handleCrash(Exception("Debug Exception"), file)
        return 1
    }

    internal fun forceTimeoutMessage(context: CommandContext<CommandSourceStack>): Int {
        handleSparkWorldTimeOut(TimeoutException("Debug timeout"))
        return 1
    }

    /*
     *  LINK
     */

    internal fun status(context: CommandContext<CommandSourceStack>): Int {
        val uuid = context.source.entityOrException.uuid.toKotlinUuid()

        var status: LinkStatus? = null
        runBlocking {
            status = LinkServiceImpl.getLinkStatus(uuid)
        }

        val source = context.source
        if (status == null) {
            source.sendSuccess(
                {
                    Component.literal("${uuid.toHexDashString()} is currently not linked or being linked.")
                },
                false
            )
        } else {
            val expirationTime = status.expires - Clock.System.now()
            var timeString = "Time left: N/A"
            if (!expirationTime.isNegative()) {
                timeString =
                    "Time left: ${expirationTime.toDateTimePeriod().minutes}m ${expirationTime.toDateTimePeriod().seconds % 60}s"
            }
            source.sendSuccess(
                {
                    Component.literal(
                        """
                        Link status for ${uuid.toHexDashString()}:
                        Verified: ${status.verified}
                        $timeString
                        Linked to ${status.snowflake}
                    """.trimIndent()
                    )
                },
                false
            )
        }
        return 1
    }

    internal fun verify(context: CommandContext<CommandSourceStack>): Int {
        try {
            val code = StringArgumentType.getString(context, "code")
            val uuid = context.source.entityOrException.uuid.toKotlinUuid()

            var result: VerificationResult? = null
            runBlocking {
                result = LinkServiceImpl.attemptVerification(uuid, code)
            }

            if (result?.success == true) {
                context.source.sendSuccess(
                    { Component.literal(result.message).withStyle(ChatFormatting.DARK_GREEN) },
                    false
                )
            } else {
                context.source.sendFailure(Component.literal(result?.message ?: "null"))
            }

            return if (result?.success == true) 1 else 0
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return 0
    }

    internal fun forceLink(context: CommandContext<CommandSourceStack>): Int {
        val uuid = UuidArgument.getUuid(context, "uuid").toKotlinUuid()
        val id = StringArgumentType.getString(context, "id").toULong()

        var result: VerificationResult? = null
        runBlocking {
            result = LinkServiceImpl.forceVerification(uuid, id)
        }

        if (result?.success == true) {
            context.source.sendSuccess(
                { Component.literal(result.message).withStyle(ChatFormatting.DARK_GREEN) },
                false
            )
        } else {
            context.source.sendFailure(Component.literal(result?.message ?: "null"))
        }

        return if (result?.success == true) 1 else 0
    }

    internal fun viewLink(context: CommandContext<CommandSourceStack>): Int {
        val target = GameProfileArgument.getGameProfiles(context, "target").first()

        var status: LinkStatus? = null
        runBlocking {
            status = LinkServiceImpl.getLinkStatus(target.id.toKotlinUuid())
        }

        val source = context.source
        if (status == null) {
            source.sendSuccess(
                {
                    Component.literal(
                        "${
                            target.id.toKotlinUuid().toHexDashString()
                        } is currently not linked or being linked."
                    )
                },
                false
            )
        } else {
            val expirationTime = status.expires - Clock.System.now()
            var timeString = "Time left: N/A"
            if (!expirationTime.isNegative()) {
                timeString =
                    "Time left: ${expirationTime.toDateTimePeriod().minutes}m ${expirationTime.toDateTimePeriod().seconds % 60}s"
            }
            source.sendSuccess(
                {
                    Component.literal(
                        """
                        Link status for ${target.id.toKotlinUuid().toHexDashString()}:
                        Verified: ${status.verified}
                        $timeString
                        Linked to ${status.snowflake}
                    """.trimIndent()
                    )
                },
                false
            )
        }
        return 1
    }
}
package org.tywrapstudios.khat.command

import com.mojang.brigadier.context.CommandContext
import com.uchuhimo.konf.source.toml.toToml
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import org.tywrapstudios.khat.KhatMod
import org.tywrapstudios.khat.api.McPlayer
import org.tywrapstudios.khat.compat.handleSparkWorldTimeOut
import org.tywrapstudios.khat.config.KhatSpec
import org.tywrapstudios.khat.config.globalConfig
import org.tywrapstudios.khat.config.id
import org.tywrapstudios.khat.config.webhooks
import org.tywrapstudios.khat.logic.HandleMinecraft
import java.nio.file.Files
import java.util.concurrent.TimeoutException
import kotlin.Int
import kotlin.io.path.writer
import kotlin.text.trimIndent

object CommandExecutions {
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

    internal fun dumpConfig(context: CommandContext<CommandSourceStack>): Int {
        val source = context.getSource()
        val message = """
                    --------[Config]---------
                    ${webhooks.joinToString("\n") { 
                        """${it.id}:
                            |${it.config.toToml.toText()}
                        """.trimMargin()}}
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
        } catch (_: Exception) {}
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
}
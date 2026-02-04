package org.tywrapstudios.khat.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.commands.arguments.UuidArgument

object CommandImpl {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        val root = Commands
            .literal("khat")
            .executes(CommandExecutions::root)
            .build()

        val debug = Commands
            .literal("debug")
            .requires { source ->
                source.hasPermission(3)
            }
            .build()

        val dumpConfig = Commands
            .literal("dump-config")
            .executes(CommandExecutions::dumpConfig)
            .build()

        val forceChat = Commands
            .literal("force-chat")
            .executes(CommandExecutions::forceChatMessage)
            .build()

        val forceGame = Commands
            .literal("force-game")
            .executes(CommandExecutions::forceGameMessage)
            .build()

        val forceCrash = Commands
            .literal("force-crash-message")
            .executes(CommandExecutions::forceCrashMessage)
            .build()

        val forceTimeout = Commands
            .literal("force-timeout-message")
            .executes(CommandExecutions::forceTimeoutMessage)
            .build()

        val link = Commands
            .literal("link")
            .build()

        val status = Commands
            .literal("status")
            .executes(CommandExecutions::status)
            .build()

        val verify = Commands
            .literal("verify")
            .build()

        val codeArg = Commands
            .argument("code", StringArgumentType.greedyString())
            .executes(CommandExecutions::verify)
            .build()

        val forceLink = Commands
            .literal("force-link")
            .requires { source ->
                source.hasPermission(3)
            }
            .build()

        val uuidArg = Commands
            .argument("uuid", UuidArgument.uuid())
            .build()

        val idArg = Commands
            .argument("id", StringArgumentType.word())
            .executes(CommandExecutions::forceLink)
            .build()

        val viewLink = Commands
            .literal("view-link")
            .requires { source ->
                source.hasPermission(3)
            }
            .build()

        val targetArg = Commands
            .argument("target", GameProfileArgument.gameProfile())
            .executes(CommandExecutions::viewLink)
            .build()

        /* Root */
        dispatcher.root.addChild(root)

        /* Debug */
        root.addChild(debug)
        debug.addChild(dumpConfig)
        debug.addChild(forceChat)
        debug.addChild(forceGame)
        debug.addChild(forceCrash)
        debug.addChild(forceTimeout)

        /* Link */
        root.addChild(link)
        link.addChild(status)
        link.addChild(verify)
        verify.addChild(codeArg)
        link.addChild(forceLink)
        forceLink.addChild(uuidArg)
        forceLink.addChild(idArg)
        link.addChild(viewLink)
        viewLink.addChild(targetArg)
    }
}
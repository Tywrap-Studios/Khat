package org.tywrapstudios.khat.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands

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
            .literal("dump_config")
            .executes(CommandExecutions::dumpConfig)
            .build()

        val forceChat = Commands
            .literal("force_chat")
            .executes(CommandExecutions::forceChatMessage)
            .build()

        val forceGame = Commands
            .literal("force_game")
            .executes(CommandExecutions::forceGameMessage)
            .build()

        val forceCrash = Commands
            .literal("force_crash_message")
            .executes(CommandExecutions::forceCrashMessage)
            .build()

        val forceTimeout = Commands
            .literal("force_timeout_message")
            .executes(CommandExecutions::forceTimeoutMessage)
            .build()

        /* Debug */
        root.addChild(debug)
        debug.addChild(dumpConfig)
        debug.addChild(forceChat)
        debug.addChild(forceGame)
        debug.addChild(forceCrash)
        debug.addChild(forceTimeout)

        /* Root */
        dispatcher.root.addChild(root)
    }
}
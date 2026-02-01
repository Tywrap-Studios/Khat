package org.tywrapstudios.khat.platform.kamera.command

import net.minecraft.server.dedicated.DedicatedServer
import org.tywrapstudios.kamera.api.CommandService
import org.tywrapstudios.khat.KhatMod

class CommandServiceImpl : CommandService {
    private val source = MRpcCommandSource(KhatMod.SERVER)

    override suspend fun run(command: String): String {
        var command = command
        if (command.startsWith("/")) {
            command = command.replaceFirst("/", "")
        }
        if (KhatMod.SERVER.isDedicatedServer) {
            val server = KhatMod.SERVER as DedicatedServer
            source.reset()
            server.executeBlocking {
                server.commands
                    .performPrefixedCommand(source.createCommandSourceStack(), command)
            }
            return source.getResponse().ifEmpty { "No or empty response" }
        }
        return "Server is not dedicated"
    }
}
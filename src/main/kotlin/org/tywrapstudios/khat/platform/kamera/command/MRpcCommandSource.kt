package org.tywrapstudios.khat.platform.kamera.command

import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class MRpcCommandSource(val server: MinecraftServer) : CommandSource {
    private val component = Component.literal("mRpc")
    private val responseBuffer = StringBuffer()

    fun reset() {
        this.responseBuffer.setLength(0)
    }

    fun getResponse(): String {
        return this.responseBuffer.toString()
    }

    fun createCommandSourceStack(): CommandSourceStack {
        val serverLevel: ServerLevel = server.overworld()
        return CommandSourceStack(
            this,
            Vec3.atLowerCornerOf(serverLevel.sharedSpawnPos),
            Vec2.ZERO,
            serverLevel,
            4,
            "mRpc",
            component,
            this.server,
            null
        )
    }

    override fun sendSystemMessage(component: Component) {
        this.responseBuffer.append(component.string)
    }

    override fun acceptsSuccess(): Boolean {
        return true
    }

    override fun acceptsFailure(): Boolean {
        return true
    }

    override fun shouldInformAdmins(): Boolean {
        return true
    }
}
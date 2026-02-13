package org.tywrapstudios.khat.platform.kamera.command

import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
//? if 1.21.11
//import net.minecraft.server.permissions.LevelBasedPermissionSet
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3

class MRpcCommandSource(val server: MinecraftServer) : CommandSource {
    private val component = Component.literal("mRpc")
    private val responseBuffer = StringBuffer()

    fun reset() {
        this.responseBuffer.setLength(0)
    }

    fun getBuiltUpResponse(): String {
        return this.responseBuffer.toString()
    }

    fun createCommandSourceStack(): CommandSourceStack {
        val serverLevel: ServerLevel = server.overworld()
        return CommandSourceStack(
            this,
            //? if 1.21.11 {
            /*Vec3.atLowerCornerOf(serverLevel.getRespawnData().pos()),
            *///?} else {
            Vec3.atLowerCornerOf(serverLevel.sharedSpawnPos),
            //?}
            Vec2.ZERO,
            serverLevel,
            //? if 1.21.11 {
            /*LevelBasedPermissionSet.OWNER,
            *///?} else {
            4,
            //?}
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
package org.tywrapstudios.krapher.extensions.minecraft

import dev.kord.common.entity.PresenceStatus
import dev.kordex.core.extensions.Extension
import dev.kordex.core.utils.scheduling.Task
import kotlinx.rpc.withService
import org.tywrapstudios.kamera.api.ServerStatsService
import org.tywrapstudios.krapher.BotInitializer
import org.tywrapstudios.krapher.KameraClient
import kotlin.time.Duration.Companion.seconds

class MiscExtension : Extension() {
    override val name: String = "krapher.minecraft.misc"

    var statusTask: Task? = null

    override suspend fun setup() {
        statusTask = BotInitializer.scheduler.schedule(
            20.seconds,
            name = "Status Refresher Task",
            repeat = true
        ) {
            val triple = try {
                val client = KameraClient.getClient().withService<ServerStatsService>()
                Triple(client.isOnline(), client.playerCount(), client.maximumPlayers())
            } catch (e: Exception) {
                e.printStackTrace()
                Triple(false, 0, 0)
            }
            kord.editPresence {
                this.status = if (triple.first) PresenceStatus.Online else PresenceStatus.Idle
                watching("${triple.second}/${triple.third} people playing.")
            }
        }
    }
}
package org.tywrapstudios.khat.platform.kamera

import org.tywrapstudios.kamera.api.ServerStatsService
import org.tywrapstudios.khat.KhatMod

object ServerStatsServiceImpl : ServerStatsService {
    override suspend fun isOnline(): Boolean = KhatMod.SERVER.isRunning
    override suspend fun playerCount(): Int = KhatMod.SERVER.playerCount
    override suspend fun maximumPlayers(): Int = KhatMod.SERVER.maxPlayers
}
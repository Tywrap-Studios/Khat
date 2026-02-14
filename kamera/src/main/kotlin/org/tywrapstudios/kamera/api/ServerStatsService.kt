package org.tywrapstudios.kamera.api

import kotlinx.rpc.annotations.Rpc

@Rpc
interface ServerStatsService {
    suspend fun isOnline(): Boolean
    suspend fun playerCount(): Int
    suspend fun maximumPlayers(): Int
}
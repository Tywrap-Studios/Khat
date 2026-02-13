package org.tywrapstudios.kamera.api

import kotlinx.rpc.annotations.Rpc

@Rpc
interface HealthService {
    suspend fun isOnline(): Boolean
}
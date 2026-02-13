package org.tywrapstudios.khat.platform.kamera

import org.tywrapstudios.kamera.api.HealthService
import org.tywrapstudios.khat.KhatMod

object HealthServiceImpl : HealthService {
    override suspend fun isOnline(): Boolean {
        return KhatMod.SERVER.isRunning
    }
}
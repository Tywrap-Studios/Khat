package org.tywrapstudios.kamera.api

import kotlinx.rpc.annotations.Rpc

@Rpc
interface CommandService {
    suspend fun run(command: String): String
}
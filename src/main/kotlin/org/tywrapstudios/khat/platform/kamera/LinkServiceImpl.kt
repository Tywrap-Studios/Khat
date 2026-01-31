@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.khat.platform.kamera

import org.tywrapstudios.kamera.api.LinkCode
import org.tywrapstudios.kamera.api.LinkService
import org.tywrapstudios.kamera.api.LinkStatus
import org.tywrapstudios.kamera.api.VerificationResult
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class LinkServiceImpl : LinkService {
    override suspend fun generateCode(
        uuid: Uuid,
        snowflake: ULong
    ): LinkCode {
        val code = CodeGenerator.generateCode()
        val expires = Clock.System.now() + 15.minutes
        return LinkCode(
            code,
            expires
        )
    }

    override suspend fun getLinkStatus(uuid: Uuid): LinkStatus {
        TODO("Not yet implemented")
    }

    override suspend fun attemptVerification(
        uuid: Uuid,
        code: String
    ): VerificationResult {
        TODO("Not yet implemented")
    }

    override suspend fun forceVerification(uuid: Uuid): VerificationResult {
        TODO("Not yet implemented")
    }
}
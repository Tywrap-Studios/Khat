@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.khat.platform.kamera

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.tywrapstudios.kamera.api.LinkCode
import org.tywrapstudios.kamera.api.LinkService
import org.tywrapstudios.kamera.api.LinkStatus
import org.tywrapstudios.kamera.api.VerificationResult
import org.tywrapstudios.khat.database.tables.LinkTable
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object LinkServiceImpl : LinkService {
    override suspend fun generateCode(
        uuid: Uuid,
        snowflake: ULong
    ): LinkCode? {
        var linkCode: LinkCode? = null

        val code = CodeGenerator.generateCode()
        val expires = Clock.System.now() + 15.minutes
        transaction {
            SchemaUtils.create(LinkTable)

            val query = LinkTable.selectAll().where { LinkTable.uuid eq uuid }

            if (!query.empty()) {
                if (query.first().let { it[LinkTable.verified] }) {
                    return@transaction
                }
            }

            LinkTable.replace {
                it[id] = snowflake
                it[LinkTable.uuid] = uuid
                it[LinkTable.code] = code
                it[LinkTable.expires] = expires
                it[LinkTable.verified] = false
            }
            linkCode = LinkCode(code, expires)
        }
        return linkCode
    }

    override suspend fun getLinkStatus(uuid: Uuid): LinkStatus? {
        var status: LinkStatus? = null

        transaction {
            SchemaUtils.create(LinkTable)

            LinkTable.selectAll().where { LinkTable.uuid eq uuid }.forEach {
                status = LinkStatus(
                    it[LinkTable.uuid],
                    it[LinkTable.id],
                    it[LinkTable.expires],
                    it[LinkTable.verified]
                )
            }
        }

        return status
    }

    override suspend fun getLinkStatusBySnowflake(snowflake: ULong): LinkStatus? {
        var status: LinkStatus? = null

        transaction {
            SchemaUtils.create(LinkTable)

            LinkTable.selectAll().where { LinkTable.id eq snowflake }.forEach {
                status = LinkStatus(
                    it[LinkTable.uuid],
                    it[LinkTable.id],
                    it[LinkTable.expires],
                    it[LinkTable.verified]
                )
            }
        }

        return status
    }

    override suspend fun attemptVerification(
        uuid: Uuid,
        code: String
    ): VerificationResult {
        var result = VerificationResult(false, "No result given")
        transaction {
            SchemaUtils.create(LinkTable)

            val query = LinkTable.selectAll().where { LinkTable.uuid eq uuid }

            if (query.empty()) {
                result = VerificationResult(false, "No entry found for ${uuid.toHexDashString()}")
                return@transaction
            }
            if (query.first().let { it[LinkTable.verified] }) {
                result = VerificationResult(false, "Already verified")
                return@transaction
            }
            if (query.first().let { it[LinkTable.expires] < Clock.System.now() }) {
                result = VerificationResult(false, "Code expired")
                LinkTable.deleteWhere { LinkTable.uuid eq uuid }
                return@transaction
            }

            query.first().let {
                if (it[LinkTable.code] == code) {
                    try {
                        LinkTable.update({ LinkTable.uuid eq uuid }) { link ->
                            link[LinkTable.code] = "0000000000000000"
                            link[LinkTable.verified] = true
                        }
                        result = VerificationResult(true, "User successfully linked!")
                    } catch (e: Exception) {
                        result = VerificationResult(false, "Error: ${e.message}")
                    }
                }
            }
        }
        return result
    }

    override suspend fun forceVerification(uuid: Uuid, snowflake: ULong): VerificationResult {
        var result = VerificationResult(false, "")
        transaction {
            SchemaUtils.create(LinkTable)

            LinkTable.replace {
                it[id] = snowflake
                it[LinkTable.uuid] = uuid
                it[LinkTable.code] = "0000000000000000"
                it[LinkTable.expires] = Clock.System.now()
                it[LinkTable.verified] = true
            }
            result = VerificationResult(true, "User successfully linked!")
        }
        return result
    }

    override suspend fun forceExisting(uuid: Uuid): VerificationResult {
        var result = VerificationResult(false, "")
        transaction {
            SchemaUtils.create(LinkTable)

            val query = LinkTable.selectAll().where { LinkTable.uuid eq uuid }

            if (query.empty()) {
                result = VerificationResult(false, "No entry found for ${uuid.toHexDashString()}")
                return@transaction
            }
            if (query.first().let { it[LinkTable.verified] }) {
                result = VerificationResult(false, "Already verified")
                return@transaction
            }

            query.first().let {
                LinkTable.update({ LinkTable.uuid eq uuid }) { link ->
                    link[LinkTable.code] = "0000000000000000"
                    link[LinkTable.verified] = true
                }
            }
        }
        return result
    }

    override suspend fun forceExistingBySnowflake(snowflake: ULong): VerificationResult {
        var result = VerificationResult(false, "")
        transaction {
            SchemaUtils.create(LinkTable)

            val query = LinkTable.selectAll().where { LinkTable.id eq snowflake }

            if (query.empty()) {
                result = VerificationResult(false, "No entry found for $snowflake")
                return@transaction
            }
            if (query.first().let { it[LinkTable.verified] }) {
                result = VerificationResult(false, "Already verified")
                return@transaction
            }

            query.first().let {
                LinkTable.update({ LinkTable.id eq snowflake }) { link ->
                    link[LinkTable.code] = "0000000000000000"
                    link[LinkTable.verified] = true
                }
            }
        }
        return result
    }

    override suspend fun unlink(uuid: Uuid): Boolean {
        return transaction {
            SchemaUtils.create(LinkTable)

            val query = LinkTable.selectAll().where { LinkTable.uuid eq uuid }

            if (query.empty()) {
                return@transaction false
            }

            LinkTable.deleteWhere { LinkTable.uuid eq uuid } > 0
        }
    }

    override suspend fun unlinkBySnowflake(snowflake: ULong): Boolean {
        return transaction {
            SchemaUtils.create(LinkTable)

            val query = LinkTable.selectAll().where { LinkTable.id eq snowflake }

            if (query.empty()) {
                return@transaction false
            }

            LinkTable.deleteWhere { LinkTable.id eq snowflake } > 0
        }
    }
}
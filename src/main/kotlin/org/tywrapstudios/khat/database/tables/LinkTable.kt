@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.khat.database.tables

import org.jetbrains.exposed.v1.core.Table
import org.tywrapstudios.khat.database.sql.kTimestamp
import org.tywrapstudios.khat.database.sql.kUuid
import kotlin.uuid.ExperimentalUuidApi

object LinkTable : Table() {
    val id = ulong("id").uniqueIndex()
    val uuid = kUuid("uuid").uniqueIndex()
    val code = char("code", 16)
    val expires = kTimestamp("expires")
    val verified = bool("verified")
}

package org.tywrapstudios.krapher.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.tywrapstudios.krapher.logger
import java.net.URI
import java.net.URL
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a Minecraft player's profile, in simple terms, from the Mojang API
 * at https://api.mojang.com/users/profiles/minecraft/NAME, not to be confused
 * with the more advanced [McPlayer] result from the session server.
 */
@Serializable
data class SimpleMcProfile(
	val id: String,
	val name: String,
)

/**
 * Fetches a Minecraft player profile by username from the Mojang API.
 * Returns a [McPlayer] object if successful, or null if an error occurs.
 * @param name The username of the Minecraft player.
 * @return [McPlayer] object or null if an error occurs.
 */
@OptIn(ExperimentalUuidApi::class)
fun getMcPlayer(name: String): McPlayer? {
	return try {
		val uuidUrl: URL =
			URI.create("https://api.mojang.com/users/profiles/minecraft/$name").toURL()
		val uuidResponse = uuidUrl.readText()
		val simpleProfile = Json.decodeFromString<SimpleMcProfile>(uuidResponse)
		val uuid = simpleProfile.id
		getMcPlayer(Uuid.parse(uuid))
	} catch (e: Exception) {
		logger.warn("Something went wrong while fetching Minecraft profile for username: $name")
		e.printStackTrace()
		null
	}
}

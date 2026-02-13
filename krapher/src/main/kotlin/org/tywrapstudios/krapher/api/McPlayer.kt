@file:OptIn(ExperimentalUuidApi::class)

package org.tywrapstudios.krapher.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.tywrapstudios.krapher.logger
import java.net.URI
import java.net.URL
import java.util.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Represents a Minecraft player with their ID, name, legacy status, and properties.
 * From the Mojang API at https://sessionserver.mojang.com/session/minecraft/profile/UUID.
 * @property id Player's [Uuid].
 * @property name Player name, case-sensitive.
 * @property legacy Included in response if the account has not migrated to Mojang account.
 * @property properties A list of [McPlayerProperties]. Is expected to contain only one entry.
 */
@Serializable
data class McPlayer(
	val id: String,
	val name: String,
	val legacy: Boolean? = false,
	val properties: List<McPlayerProperties>,
	val profileActions: List<String>
)

/**
 * Represents properties of a Minecraft player, including their name, signature, and value.
 * Used in the Mojang API response for player profiles.
 * @property name Name of the property. For now, the only property that exists is `textures`.
 * @property signature Signature signed with Yggdrasil private key as Base64 string, only exists when `?unsigned=false`
 * is appended to the end of the API call URL.
 * @property value Base64 string containing the value.
 * In our case, the textures value can be decoded into [PropertiesValue], which includes
 * all player textures (skin and cape).
 */
@Serializable
data class McPlayerProperties(
	val name: String,
	val signature: String? = null,
	val value: String,
)

/**
 * Represents the value of a Minecraft player's properties, including timestamp, profile ID, profile name,
 * and a map of textures.
 * This is the decoded value of the `textures` property value from [McPlayerProperties].
 * @property timestamp Unix time in milliseconds the texture is accessed.
 * @property profileId Player's [UUID] without dashes.
 * @property profileName Player name.
 * @property signatureRequired Only exists when `?unsigned=false`
 * is appended to the end of the API call URL.
 * @property textures A map of texture types ("SKIN" and "CAPE") to their corresponding [TextureObject].
 */
@Serializable
data class PropertiesValue(
	val timestamp: Int,
	val profileId: String,
	val profileName: String,
	val signatureRequired: Boolean? = false,
	val textures: Map<String, TextureObject>
)

/**
 * Represents a texture object in Minecraft, which includes a URL and optional metadata.
 * The metadata can include the model type (e.g., "slim" for Alex skin model).
 * @property url URL of the texture at http://textures.minecraft.net/texture/texture_hash.
 * @property metadata Optional metadata about the texture, such as the model type.
 */
@Serializable
data class TextureObject(val url: String, val metadata: TextureObjectMetadata? = null)

/**
 * Represents metadata for a texture object in Minecraft.
 * Currently, it only includes the model type (e.g., "slim" for Alex skin model).
 * @property model The model type of the texture, such as "slim".
 */
@Serializable
data class TextureObjectMetadata(val model: String)

/**
 * Fetches a Minecraft player profile by UUID from the Mojang API.
 * Returns a [McPlayer] object if successful, or null if an error occurs.
 * @param uuid The UUID of the Minecraft player.
 * @return [McPlayer] object or null if an error occurs.
 */
fun getMcPlayer(uuid: Uuid): McPlayer? {
	return try {
		logger.debug("Fetching Minecraft player profile for UUID: {}", uuid.toHexDashString())
		val url: URL =
			URI.create("https://sessionserver.mojang.com/session/minecraft/profile/$uuid").toURL()
		val response = url.readText()
		logger.debug("Response: $response")
		Json.decodeFromString<McPlayer>(response)
	} catch (e: Exception) {
		logger.warn("Something went wrong while fetching Minecraft profile for UUID: ${uuid.toHexDashString()}")
		e.printStackTrace()
		null
	}
}
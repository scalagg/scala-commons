/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.uuidcache

import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

abstract class UUIDCache {

    internal var uuidToUsername = ConcurrentHashMap<UUID, String>()
    internal var usernameToUuid = ConcurrentHashMap<String, UUID>()

    var alwaysFetch: Boolean = false

    fun load() {
        val newUuidToName = ConcurrentHashMap<UUID, String>()
        val newNameToUuid = ConcurrentHashMap<String, UUID>()

        val cache = fetchCache()

        cache.forEach { (uuid, name) ->
            newUuidToName[uuid] = name
            newNameToUuid[name.toLowerCase()] = uuid
        }

        uuidToUsername = newUuidToName
        usernameToUuid = newNameToUuid
    }

    fun uuid(username: String): UUID? {
        return if (alwaysFetch) {
            fetchUUID(username)
        } else {
            usernameToUuid[username.toLowerCase()]
        }
    }

    /**
     * Attempts to retrieve a username for the given [uuid]
     */
    fun name(uuid: UUID): String? {
        return if (alwaysFetch) {
            fetchUsername(uuid)
        } else {
            uuidToUsername[uuid]
        }
    }

    /**
     * Updates the cache for the given [uuid] and [username], then commits changes to Redis if [commit] is true.
     */
    fun update(uuid: UUID, username: String, commit: Boolean = true) {
        uuidToUsername[uuid] = username
        usernameToUuid[username.toLowerCase()] = uuid

        if (commit) {
            updateCache(uuid, username)
        }
    }

    fun getCachedUuids(): Set<UUID> {
        return uuidToUsername.keys
    }

    fun isCached(uuid: UUID): Boolean {
        return uuidToUsername.containsKey(uuid)
    }

    /**
     * Fetches the cache from this implementation's cache storage.
     */
    abstract fun fetchCache(): Map<UUID, String>

    abstract fun fetchUsername(uuid: UUID): String?

    abstract fun fetchUUID(username: String): UUID?

    /**
     * Writes an update to this implementation's cache storage.
     * This function should always be called asynchronously.
     */
    abstract fun updateCache(uuid: UUID, name: String)

    companion object {
        private val client = OkHttpClient()
        private val parser = JsonParser()

        /**
         * Fetches a UUID and Username pair from the Mojang API.
         * Should not be called very often as the server could be rate-limited which will cause more issues.
         */
        @JvmStatic
        fun fetchFromMojang(username: String): Optional<Pair<UUID, String>> {
            try {
                val request = Request.Builder().url("https://api.mojang.com/users/profiles/minecraft/$username").build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = parser.parse(response.body().string()).asJsonObject

                    val uuid = UUID.fromString(json.get("id").asString.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(), "$1-$2-$3-$4-$5"))
                    val name = json.get("name").asString

                    return Optional.of(Pair(uuid, name))
                }
            } catch (e: Exception) {
                Logger.getGlobal().info("Failed to retrieve user info from Mojang API: $username")
            }

            return Optional.ofNullable(null)
        }

        @JvmStatic
        fun fetchFromMojang(uuid: UUID): Optional<Pair<UUID, String>> {
            try {
                val request = Request.Builder().url("https://api.mojang.com/user/profiles/${uuid.toString().replace("-", "")}/names").build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val names = parser.parse(response.body().string()).asJsonArray.map { it.asJsonObject }
                    if (names.size == 1) {
                        return Optional.of(uuid to names.first().get("name").asString)
                    }

                    val name = names
                        .filter { it.has("changedToAt") }
                        .maxBy { it.get("changedToAt").asLong }!!.get("name").asString

                    return Optional.of(Pair(uuid, name))
                }
            } catch (e: Exception) {
                Logger.getGlobal().info("Failed to retrieve user info from Mojang API: $uuid")
            }

            return Optional.ofNullable(null)
        }
    }
}
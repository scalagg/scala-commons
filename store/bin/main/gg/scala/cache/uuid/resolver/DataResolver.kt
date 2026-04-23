package gg.scala.cache.uuid.resolver

import java.util.UUID

/**
 * @author GrowlyX
 * @since 9/13/2022
 */
interface DataResolver
{
    fun assignedTo(): ResolverType

    fun uniqueIdToUsername(uniqueId: UUID): Pair<UUID, String>?
    fun usernameToUniqueId(username: String): Pair<UUID, String>?
}

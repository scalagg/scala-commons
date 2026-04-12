package gg.scala.cache.uuid.resolver

import java.util.UUID

interface DataResolver
{
    fun assignedTo(): ResolverType

    fun uniqueIdToUsername(uniqueId: UUID): Pair<UUID, String>?
    fun usernameToUniqueId(username: String): Pair<UUID, String>?
}

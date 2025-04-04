package gg.scala.commons.scoping

import gg.scala.commons.agnostic.sync.ServerSync

/**
 * @author GrowlyX
 * @since 2/9/2025
 */
interface ServerScoped
{
    var scopes: Set<String>
    fun isGlobal() = "global" in scopes
    fun isScopedLocally() = ServerSync.local.groups.first() in scopes

    fun isApplicable() = isGlobal() || isScopedLocally()
}

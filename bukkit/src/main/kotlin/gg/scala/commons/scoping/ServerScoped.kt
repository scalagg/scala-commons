package gg.scala.commons.scoping

import gg.scala.commons.agnostic.sync.ServerSync

interface ServerScoped
{
    var scopes: Set<String>
    fun isGlobal() = "global" in scopes
    fun isScopedLocally() = ServerSync.local.groups.first() in scopes

    fun isApplicable() = isGlobal() || isScopedLocally()
}

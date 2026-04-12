package gg.scala.commons.scoping

fun <T : ServerScoped> Collection<T>.selectScoped() = firstOrNull { it.isScopedLocally() }
    ?: firstOrNull { it.isGlobal() }

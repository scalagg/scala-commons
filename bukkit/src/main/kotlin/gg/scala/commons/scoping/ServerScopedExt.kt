package gg.scala.commons.scoping

/**
 * @author GrowlyX
 * @since 2/9/2025
 */
fun <T : ServerScoped> Collection<T>.selectScoped() = firstOrNull { it.isScopedLocally() }
    ?: firstOrNull { it.isGlobal() }

package gg.scala.commons.core.mutable

/**
 * @author GrowlyX
 * @since 10/11/2022
 */
fun <L, M, R> Triple<L, M, R>.mutable() =
    MutableTriple(this.first, this.second, this.third)

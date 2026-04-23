package gg.scala.commons.core.mutable

/**
 * @author GrowlyX
 * @since 10/11/2022
 */
fun <K, V> Pair<K, V>.mutable() =
    MutablePair(this.first, this.second)

package gg.scala.commons.core.mutable

fun <K, V> Pair<K, V>.mutable() =
    MutablePair(this.first, this.second)

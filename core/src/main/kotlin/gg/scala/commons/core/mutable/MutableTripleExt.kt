package gg.scala.commons.core.mutable

fun <L, M, R> Triple<L, M, R>.mutable() =
    MutableTriple(this.first, this.second, this.third)


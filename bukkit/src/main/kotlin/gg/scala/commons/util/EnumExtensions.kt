package gg.scala.commons.util

inline fun <reified T : Enum<T>> Enum<T>.previous() = with(enumValues<T>()) {
    getOrNull(indexOf(this@previous) - 1) ?: last()
}

inline fun <reified T : Enum<T>> Enum<T>.next() = with(enumValues<T>()) {
    getOrNull(indexOf(this@next) + 1) ?: first()
}
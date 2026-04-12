package gg.scala.commons.localize

data class ComponentMapping(
    val id: String,
    val mapToValue: (Any) -> String
)

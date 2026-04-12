package gg.scala.commons.localize.placeholder

object NoOpPlaceholderProcessor : PlaceholderProcessor by (
    PlaceholderProcessor.build(Any::class.java) { _, message -> message }
)

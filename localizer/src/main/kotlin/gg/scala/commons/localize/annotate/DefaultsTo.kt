package gg.scala.commons.localize.annotate

@Target(AnnotationTarget.FUNCTION)
annotation class DefaultsTo(
    vararg val value: String
)

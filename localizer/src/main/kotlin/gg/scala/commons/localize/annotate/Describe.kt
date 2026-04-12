package gg.scala.commons.localize.annotate

@Target(AnnotationTarget.FUNCTION)
annotation class Describe(
    vararg val value: String
)

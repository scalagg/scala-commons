package gg.scala.commons.localize.annotate

/**
 * @author GrowlyX
 * @since 7/25/2023
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Describe(
    vararg val value: String
)

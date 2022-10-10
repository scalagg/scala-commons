package gg.scala.commons.annotations.plugin

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
@Target(
    AnnotationTarget.CLASS
)
@Repeatable
annotation class SoftDependency(
    val value: String
)

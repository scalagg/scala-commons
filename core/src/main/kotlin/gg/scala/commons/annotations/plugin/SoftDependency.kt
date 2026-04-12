package gg.scala.commons.annotations.plugin

@Target(
    AnnotationTarget.CLASS
)
@Repeatable
annotation class SoftDependency(
    val value: String
)

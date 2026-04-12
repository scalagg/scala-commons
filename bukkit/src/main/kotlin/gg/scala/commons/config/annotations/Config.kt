package gg.scala.commons.config.annotations

@Target(
    AnnotationTarget.CLASS
)
annotation class Config(
    val value: String,
    val lateInject: Boolean = false,
    val crossSync: Boolean = false
)

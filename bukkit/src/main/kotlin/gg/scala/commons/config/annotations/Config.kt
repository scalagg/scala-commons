package gg.scala.commons.config.annotations

/**
 * @author GrowlyX
 * @since 10/12/2022
 */
@Target(
    AnnotationTarget.CLASS
)
annotation class Config(
    val value: String,
    val lateInject: Boolean = false,
    val crossSync: Boolean = false
)

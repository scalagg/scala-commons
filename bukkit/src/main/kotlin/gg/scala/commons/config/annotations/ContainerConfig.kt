package gg.scala.commons.config.annotations

import kotlin.reflect.KClass

@Target(
    AnnotationTarget.CLASS
)
@Repeatable
annotation class ContainerConfig(
    val value: String,
    val model: KClass<out Any>,
    val lateInject: Boolean = false,
    val crossSync: Boolean = false
)

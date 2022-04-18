package gg.scala.commons.mappings

import net.evilblock.cubed.util.ServerVersion

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
@Target(
    AnnotationTarget.CLASS
)
annotation class MinecraftServerTarget(
    val value: ServerVersion
)

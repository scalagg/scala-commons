package gg.scala.commons.localization.container.annotations

import gg.scala.commons.localization.LocalizationLoader

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
@Target(
    AnnotationTarget.CLASS
)
@Repeatable
annotation class Localized(
    val value: String,
    val loader: LocalizationLoader =
        LocalizationLoader.YAML
)

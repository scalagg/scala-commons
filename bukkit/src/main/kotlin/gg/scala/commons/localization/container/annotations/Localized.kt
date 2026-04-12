package gg.scala.commons.localization.container.annotations

import gg.scala.commons.localization.LocalizationLoader

@Target(
    AnnotationTarget.CLASS
)
@Repeatable
annotation class Localized(
    val value: String,
    val loader: LocalizationLoader =
        LocalizationLoader.YAML
)

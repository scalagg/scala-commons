package gg.scala.commons.localize

import gg.scala.commons.localize.annotate.Self
import java.lang.reflect.Parameter

data class MethodDescriptor(
    val id: String,
    val colorize: Boolean,
    val description: List<String>,
    val defaultValue: List<String>,
    val replacements: List<ParameterDescriptor>,
    val static: Boolean,
    val identityIndex: Int = replacements
        .indexOfFirst {
            it.parameter.isAnnotationPresent(Self::class.java)
        }
)

data class ParameterDescriptor(
    val id: String,
    val parameter: Parameter,
    val component: String? = null,
)

package gg.scala.commons.localize

import java.lang.reflect.Method
import kotlin.reflect.KClass

internal object LocalizerInternals
{
    fun generateSnakeCaseID(method: Method) = method.name
        .split("(?=[A-Z])".toRegex())
        .joinToString("_") { it.lowercase() }

    fun generateFileName(kClass: KClass<*>) = kClass.java.simpleName
        .removeSuffix("Lang")
        .lowercase()
}

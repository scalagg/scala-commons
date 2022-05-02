package gg.scala.commons.replacement

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
interface Replacement
{
    fun id(): String

    val replacements: ReplacementBuilder
}

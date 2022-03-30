package gg.scala.commons.localize

/**
 * @author GrowlyX
 * @since 3/30/2022
 */
enum class LocalizationContext(
    private val extended: String
)
{
    EN_US("English (US)"),
    EN_UK("English (UK)"),
    ES("Español");

    companion object
    {
        @JvmStatic
        val DEFAULT = EN_US
    }
}

package gg.scala.commons.adventure

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * @author GrowlyX
 * @since 4/28/2022
 */
class ScalaMiniMessage(
    private val component: Component
)
{
    fun serialized(): String
    {
        return LegacyComponentSerializer
            .legacySection().serialize(this.component)
    }
}

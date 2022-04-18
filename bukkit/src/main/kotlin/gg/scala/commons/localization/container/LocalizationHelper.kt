package gg.scala.commons.localization.container

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.localization.LocalizationContext
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 4/18/2022
 */
class LocalizationHelper(
    private val plugin: ExtendedScalaPlugin
)
{
    private val audiences =
        BukkitAudiences.create(plugin)

    /**
     * Send localized messages from a contextual container.
     */
    fun sendLocalized(
        player: Player, key: String,
        container: LocalizationContainerContextual,
        vararg replacements: Pair<String, Any>
    )
    {
        // TODO: 4/18/2022 use player's locale
        val contextual = container
            .contextual(LocalizationContext.EN_US)

        val parsed = contextual
            .getComponent(key, *replacements)

        val audience = this
            .audiences
            .player(player)

        audience.sendMessage(parsed)
    }
}

package gg.scala.commons.localization.container

import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.localization.LocalizationContext
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.entity.Player

class LocalizationHelper(
    private val plugin: ExtendedScalaPlugin
)
{
    private val audiences =
        BukkitAudiences.create(plugin)

    fun sendLocalized(
        player: Player, key: String,
        container: LocalizationContainerContextual,
        vararg replacements: Pair<String, Any>
    )
    {

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

package gg.scala.commons.tablist

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 2/19/2024
 */
interface TablistSystemProvider
{
    fun configure()

    fun initialize(player: Player)
    fun create(player: Player, display: Boolean)
    fun destroy(player: Player)
}

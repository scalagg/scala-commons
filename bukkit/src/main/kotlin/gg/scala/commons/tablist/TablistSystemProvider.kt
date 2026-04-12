package gg.scala.commons.tablist

import org.bukkit.entity.Player

interface TablistSystemProvider
{
    fun configure()

    fun initialize(player: Player)
    fun create(player: Player, display: Boolean)
    fun destroy(player: Player)
}

/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

@AutoRegister
@CommandAlias("spawn-armor-stand")
@CommandPermission("op")
object SpawnArmorStandCommand : ScalaCommand()
{
    fun execute(player: Player)
    {
        val armorStand = player.world
            .spawn(player.location, ArmorStand::class.java)

        armorStand.setBasePlate(false)
        armorStand.setArms(true)

        armorStand.isSmall = true
        armorStand.isMarker = false
    }
}

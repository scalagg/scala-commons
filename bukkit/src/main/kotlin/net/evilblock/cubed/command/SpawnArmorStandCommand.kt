

package net.evilblock.cubed.command

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.issuer.ScalaPlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

@AutoRegister
object SpawnArmorStandCommand : ScalaCommand()
{
    @CommandPermission("op")
    @CommandAlias("spawn-armor-stand")
    fun onArmorStandSpawn(player: ScalaPlayer)
    {
        val armorStand = player.bukkit().world
            .spawn(player.bukkit().location, ArmorStand::class.java)

        armorStand.setBasePlate(false)
        armorStand.setArms(true)

        armorStand.isSmall = true
        armorStand.isMarker = false
    }
}

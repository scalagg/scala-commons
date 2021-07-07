package net.evilblock.cubed.util.bukkit.player

import org.bukkit.entity.Player

open class PlayerSnapshot(player: Player) {

    val location = player.location.clone()

    val inventoryContents = player.inventory.contents
    val armorContents = player.inventory.armorContents

    val gameMode = player.gameMode
    val health = player.health
    val foodLevel = player.foodLevel

    val flying = player.isFlying
    val allowFly = player.allowFlight

    fun restore(player: Player) {
        player.teleport(location)
        player.inventory.contents = inventoryContents
        player.inventory.armorContents = armorContents
        player.gameMode = gameMode
        player.health = health
        player.foodLevel = foodLevel
        player.isFlying = flying
        player.allowFlight = allowFly
        player.updateInventory()
    }

}
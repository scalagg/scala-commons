package net.evilblock.cubed.entity.animation

import net.evilblock.cubed.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface EntityAnimationType {

    fun getName(): String

    fun getIcon(): ItemStack

    fun startProcedure(player: Player, entity: Entity, complete: (EntityAnimation) -> Unit)

}
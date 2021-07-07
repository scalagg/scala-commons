package net.evilblock.cubed.entity.npc.command.parameter

import net.evilblock.cubed.command.data.parameter.ParameterType
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class NpcEntityParameterType : ParameterType<NpcEntity> {

    override fun transform(sender: CommandSender, source: String): NpcEntity? {
        var entity: Entity? = null
        try {
            entity = EntityHandler.getEntityById(source.toInt())
        } catch (e: Exception) {
            try {
                entity = EntityHandler.getEntityByUuid(UUID.fromString(source))
            } catch (e: Exception) {
                sender.sendMessage("${ChatColor.RED}Couldn't find an NPC from your input: `${ChatColor.WHITE}$source${ChatColor.RED}`")
            }
        }

        if (entity is Entity && entity !is NpcEntity) {
            sender.sendMessage("${ChatColor.RED}Found an entity from your input, but it's not an NPC!")
        }

        return if (entity is NpcEntity) {
            entity
        } else {
            null
        }
    }

    override fun tabComplete(player: Player, flags: Set<String>, source: String): List<String> {
        return emptyList() // return nearby entities, sorted by distance
    }

}
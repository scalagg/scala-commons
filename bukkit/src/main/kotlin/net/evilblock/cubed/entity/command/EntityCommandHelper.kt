package net.evilblock.cubed.entity.command

import co.aikar.commands.ConditionFailedException
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.entity.Player

object EntityCommandHelper
{
    fun teleportToAnEntity(player: Player, entity: Entity)
    {
        player.teleport(entity.location)
        player.sendMessage(
            "${CC.SEC}You've been teleported to ${entity.getTypeName()} ${CC.PRI}#${entity.id - Entity.initialId}${CC.SEC}."
        )
    }

    inline fun <reified T : Entity> listEntities(player: Player)
    {
        val entities = EntityHandler
            .getEntitiesOfType<T>()
            .sortedBy { it.id }

        val entityType = T::class.java.simpleName
            .replace("Entity", "")

        if (entities.isEmpty())
        {
            throw ConditionFailedException("No ${entityType}s exist on this server.")
        }

        player.sendMessage("${CC.UB_PRI}$entityType Entities:")
        player.sendMessage("")

        for (entity in entities)
        {
            FancyMessage()
                .withMessage(" - #${entity.id - Entity.initialId} ")
                .withMessage("${CC.GRAY}(click to teleport)")
                .andCommandOf(
                    ClickEvent.Action.RUN_COMMAND,
                    "/${entityType.lowercase()} teleport ${entity.id}"
                )
                .sendToPlayer(player)
        }

        player.sendMessage(
            "${CC.PRI}=== ${CC.WHITE}${entities.size} total $entityType entities ${CC.PRI}==="
        )
    }
}

package net.evilblock.cubed.entity.menu.button

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ConversationUtil
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateCommandButton(private val entity: Entity) : Button() {

    override fun getName(player: Player): String {
        return "${CC.GREEN}Update Command"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf(
            "${CC.GRAY}Update the command that gets executed",
            "${CC.GRAY}when a player right-clicks this NPC.",
            "",
            "${CC.GREEN}Left-Click to update command.",
            "${CC.RED}Right-Click to unset command."
        )
    }

    override fun getMaterial(player: Player): Material {
        return Material.NAME_TAG
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            ConversationUtil.startConversation(player, object : StringPrompt() {
                override fun getPromptText(context: ConversationContext): String {
                    return "${CC.GREEN}Please input the new command..."
                }

                override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                    entity.command = input
                    context.forWhom.sendRawMessage("${CC.GREEN}Updated command!")

                    EditEntityMenu(entity).openMenu(context.forWhom as Player)

                    return Prompt.END_OF_CONVERSATION
                }
            })
        } else {
            entity.command = null
            player.sendMessage("${CC.GREEN}Unset command!")
        }
    }

}

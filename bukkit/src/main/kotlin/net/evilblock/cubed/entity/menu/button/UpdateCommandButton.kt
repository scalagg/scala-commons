package net.evilblock.cubed.entity.menu.button

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.menu.EditEntityMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.ConversationUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

class UpdateCommandButton(private val entity: Entity) : Button() {

    override fun getName(player: Player): String {
        return "${ChatColor.YELLOW}${ChatColor.BOLD}Update Command"
    }

    override fun getDescription(player: Player): List<String> {
        return listOf(
            "${ChatColor.GRAY}Update the command that gets executed",
            "${ChatColor.GRAY}when a player right-clicks this NPC.",
            "",
            "${ChatColor.GREEN}${ChatColor.BOLD}LEFT-CLICK ${ChatColor.GREEN}to update command",
            "${ChatColor.RED}${ChatColor.BOLD}RIGHT-CLICK ${ChatColor.RED}to unset command"
        )
    }

    override fun getMaterial(player: Player): Material {
        return Material.NAME_TAG
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        if (clickType.isLeftClick) {
            ConversationUtil.startConversation(player, object : StringPrompt() {
                override fun getPromptText(context: ConversationContext): String {
                    return "${ChatColor.GREEN}Please input the new command..."
                }

                override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                    entity.command = input
                    context.forWhom.sendRawMessage("${ChatColor.GREEN}Updated command!")

                    EditEntityMenu(entity).openMenu(context.forWhom as Player)

                    return Prompt.END_OF_CONVERSATION
                }
            })
        } else {
            entity.command = null
            player.sendMessage("${ChatColor.GREEN}Unset command!")
        }
    }

}
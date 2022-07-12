package net.evilblock.cubed.entity.npc.menu.button

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.text.TextSplitter
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta

class UpdateSkinButton(private val npc: NpcEntity, private val complete: (Boolean) -> Unit) : Button() {

    override fun getName(player: Player): String {
        return "${CC.GREEN}Update Skin"
    }

    override fun getDescription(player: Player): List<String> {
        return arrayListOf<String>().also { desc ->
            desc.addAll(TextSplitter.split(text = "Update this NPC's skin by fetching a real Minecraft player's skin by their username."))
            desc.add("")
            desc.add("${CC.YELLOW}Left-Click to update skin.")
        }
    }

    override fun getMaterial(player: Player): XMaterial
    {
        return XMaterial.SKELETON_SKULL
    }

    override fun getDamageValue(player: Player): Byte {
        return 3.toByte()
    }

    override fun applyMetadata(player: Player, itemMeta: ItemMeta): ItemMeta? {
        return (itemMeta as SkullMeta).also { meta -> meta.owner = player.name }
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        player.closeInventory()

        ConversationUtil.startConversation(player, object : StringPrompt() {
            override fun getPromptText(context: ConversationContext): String {
                return "${CC.GREEN}Please input the username to query. ${CC.GRAY}(Limit of 16 characters)"
            }

            override fun acceptInput(context: ConversationContext, input: String): Prompt? {
                if (input.equals("cancel", ignoreCase = true)) {
                    context.forWhom.sendRawMessage("${CC.YELLOW}No changes made to NPC.")
                    return Prompt.END_OF_CONVERSATION
                }

                assert(input.length in 1..16) { "Username is too long! (${input.length} > 16)" }
                assert(input.matches("[a-zA-Z0-9_]{1,16}".toRegex())) { "Username is invalid! [a-zA-Z0-9_]" }

                Tasks.async {
                    npc.updateTextureByUsername(username = input, onComplete = { success, thrown ->
                        if (success) {
                            context.forWhom.sendRawMessage("${CC.GREEN}Successfully updated texture!")

                            Tasks.async {
                                EntityHandler.close()
                            }
                        } else {
                            if (thrown == null) {
                                context.forWhom.sendRawMessage("${CC.RED}Failed to update texture.")
                            } else {
                                context.forWhom.sendRawMessage("${CC.RED}${thrown.message}.")
                            }
                        }

                        complete.invoke(success)
                    })
                }

                return Prompt.END_OF_CONVERSATION
            }
        })
    }

}

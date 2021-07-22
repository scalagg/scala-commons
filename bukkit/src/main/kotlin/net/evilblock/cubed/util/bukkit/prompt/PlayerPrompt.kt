/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit.prompt

import net.evilblock.cubed.Cubed
//import net.evilblock.cubed.store.uuidcache.UUIDCache
import net.evilblock.cubed.util.bukkit.ConversationUtil
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import java.util.*

class PlayerPrompt(private val useFunc: (UUID) -> Unit) : StringPrompt() {

    override fun getPromptText(context: ConversationContext): String {
        return "${ChatColor.GREEN}Please input the player's username or UUID."
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
//        var uuid: UUID?
//        try {
//            uuid = UUID.fromString(input)
//        } catch (e: Exception) {
//            uuid = Cubed.instance.uuidCache.uuid(input)
//            if (uuid == null) {
//                val optionalProfile = UUIDCache.fetchFromMojang(input)
//                if (optionalProfile.isPresent) {
//                    uuid = optionalProfile.get().first
//                }
//            }
//        }

//        if (uuid == null) {
//            context.forWhom.sendRawMessage("${ChatColor.RED}Your input did not match a player username or player UUID.")
//        } else {
//            useFunc.invoke(uuid)
//        }

        return Prompt.END_OF_CONVERSATION
    }

    fun start(player: Player) {
        ConversationUtil.startConversation(player, this)
    }

}
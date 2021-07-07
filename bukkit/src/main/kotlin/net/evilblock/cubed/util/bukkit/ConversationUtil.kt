/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import net.evilblock.cubed.Cubed
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.Prompt
import org.bukkit.entity.Player

object ConversationUtil {

    @JvmStatic
    fun startConversation(player: Player, prompt: Prompt) {
        if (player.openInventory != null) {
            player.closeInventory()
        }

        val factory = ConversationFactory(Cubed.instance)
            .withModality(false)
            .withFirstPrompt(prompt)
            .withLocalEcho(false)
            .thatExcludesNonPlayersWithMessage("Go away evil console!")

        player.beginConversation(factory.buildConversation(player))
    }

}
/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit.prompt

import net.evilblock.cubed.util.math.Numbers
import net.evilblock.cubed.util.bukkit.ConversationUtil
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player

class PricePrompt(private val useFunc: (Double) -> Unit) : StringPrompt() {

    override fun getPromptText(context: ConversationContext): String {
        return "${ChatColor.GREEN}Please input a price. ${ChatColor.GRAY}(Format: 1.25M, 1,250,000.00)"
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        try {
            val number = Numbers.parseInput(input)

            useFunc.invoke(number.toDouble())
        } catch (e: Exception) {
            context.forWhom.sendRawMessage("${ChatColor.RED}Couldn't parse number from input: ${ChatColor.WHITE}${input}")
        }

        return Prompt.END_OF_CONVERSATION
    }

    fun start(player: Player) {
        ConversationUtil.startConversation(player, this)
    }

}
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

class NumberPrompt : StringPrompt() {

    private var promptText: String = "${ChatColor.GREEN}Please input a number."
    private var use: ((Number) -> Unit)? = null
    private var intercept: ((String) -> Boolean)? = null

    fun withText(text: String): NumberPrompt {
        this.promptText =  text
        return this
    }

    fun acceptInput(use: (Number) -> Unit): NumberPrompt {
        this.use = use
        return this
    }

    fun beforeAccept(intercept: (String) -> Boolean): NumberPrompt {
        this.intercept = intercept
        return this
    }

    override fun getPromptText(context: ConversationContext): String {
        return promptText
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        if (intercept != null) {
            if (intercept!!.invoke(input)) {
                return Prompt.END_OF_CONVERSATION
            }
        }

        if (!NUMBER_REGEX.matches(input)) {
            context.forWhom.sendRawMessage("${ChatColor.RED}Your input doesn't match the expected regex: ${NUMBER_REGEX.pattern}")
            return null
        }

        try {
            val number = Numbers.parseInput(input)
            use!!.invoke(number)
        } catch (e: Exception) {
            context.forWhom.sendRawMessage("${ChatColor.RED}Couldn't parse number from input: ${ChatColor.WHITE}${input}")
        }

        return Prompt.END_OF_CONVERSATION
    }

    fun start(player: Player) {
        if (use == null) {
            throw IllegalStateException("No use function applied")
        }

        ConversationUtil.startConversation(player, this)
    }

    companion object {
        val NUMBER_REGEX = "(-)?(^\\d{1,3}(,\\d{3})*)*(\\.?[0-9]*)?".toRegex()
    }

}
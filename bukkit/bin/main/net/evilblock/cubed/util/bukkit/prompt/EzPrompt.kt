/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit.prompt

import net.evilblock.cubed.util.bukkit.ConversationUtil
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player

/**
 * Soon to be deprecated. Use [InputPrompt] instead.
 */
class EzPrompt(
    private var promptText: String = "",
    private var charLimit: Int = -1,
    private var regexList: MutableList<Regex> = arrayListOf(),
    private var complete: ((String) -> Unit)? = null
) : StringPrompt() {

    override fun getPromptText(context: ConversationContext): String {
        return promptText
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        if (charLimit != -1) {
            if (input.length > charLimit) {
                context.forWhom.sendRawMessage("${ChatColor.RED}Input text is too long! (${input.length} > ${charLimit})")
                return Prompt.END_OF_CONVERSATION
            }
        }

        if (regexList.isNotEmpty()) {
            for (regex in regexList) {
                if (!input.matches(regex)) {
                    context.forWhom.sendRawMessage("${ChatColor.RED}Input text does not match regex pattern ${regex.pattern}.")
                    return Prompt.END_OF_CONVERSATION
                }
            }
        }

        complete!!.invoke(input)

        return Prompt.END_OF_CONVERSATION
    }

    fun start(player: Player) {
        ConversationUtil.startConversation(player, this)
    }

    class Builder {
        val prompt: EzPrompt = EzPrompt()

        fun promptText(text: String): Builder {
            prompt.promptText = text
            return this
        }

        fun charLimit(limit: Int): Builder {
            prompt.charLimit = limit
            return this
        }

        fun regex(regex: Regex): Builder {
            prompt.regexList.add(regex)
            return this
        }

        fun acceptInput(lambda: (String) -> Unit): Builder {
            prompt.complete = lambda
            return this
        }

        fun build(): EzPrompt {
            assert(prompt.promptText != "") { "No prompt text has been applied to Builder" }
            assert(prompt.complete != null) { "No success function has been applied to Builder" }
            return prompt
        }

        companion object {
            @JvmStatic
            fun copy(builder: Builder): Builder {
                val newBuilder = Builder()
                newBuilder.prompt.promptText = builder.prompt.promptText
                newBuilder.prompt.charLimit = builder.prompt.charLimit
                newBuilder.prompt.regexList = builder.prompt.regexList
                newBuilder.prompt.complete = builder.prompt.complete
                return newBuilder
            }
        }
    }

    companion object {
        val NAME_PROMPT = "${ChatColor.GREEN}Please input a name. ${ChatColor.GRAY}(Colors supported, limit of 48 characters)"
        val IDENTIFIER_PROMPT = "${ChatColor.GREEN}Please input a unique ID. ${ChatColor.GRAY}(Limit of 16 characters)"
        val IDENTIFIER_REGEX = "[a-zA-Z_\\-0-9]*".toRegex()
    }

}
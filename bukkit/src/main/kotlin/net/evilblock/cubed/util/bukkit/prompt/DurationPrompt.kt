

package net.evilblock.cubed.util.bukkit.prompt

import net.evilblock.cubed.util.time.DateUtil
import net.evilblock.cubed.util.bukkit.ConversationUtil
import net.evilblock.cubed.util.time.Duration
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import java.lang.Exception

class DurationPrompt(private val use: (Duration) -> Unit) : StringPrompt() {

    override fun getPromptText(context: ConversationContext): String {
        return "${ChatColor.GREEN}Please input a duration."
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        if (input.equals("perm", ignoreCase = true) || input.equals("permanent", ignoreCase = true)) {
            use.invoke(Duration(Long.MAX_VALUE))
            return Prompt.END_OF_CONVERSATION
        }

        try {
            use.invoke(Duration(DateUtil.parseDuration(input)))
        } catch (exception: Exception) {
            context.forWhom.sendRawMessage("${ChatColor.RED}Invalid duration.")
        }

        return Prompt.END_OF_CONVERSATION
    }

    fun start(player: Player) {
        ConversationUtil.startConversation(player, this)
    }

}
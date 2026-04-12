package gg.scala.commons.util.prompts

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.entity.Player
import java.util.LinkedList

class CollectResponsesPrompt(private val player: Player)
{
    private val responses = mutableMapOf<String, String>()
    private val pendingPrompts = LinkedList<Pair<String, String>>()

    private lateinit var aborted: () -> Unit
    private lateinit var responded: (Map<String, String>) -> Unit

    fun with(
        key: String,
        title: String
    ) = apply {
        pendingPrompts += key to title
    }

    private fun startChain()
    {
        val first = pendingPrompts.pop()
        InputPrompt()
            .withText("${CC.YELLOW}Enter a: ${CC.WHITE}${first.second} ${CC.GRAY}(Type: stop to stop)")
            .acceptInput { _, input ->
                Button.playNeutral(player)
                responses[first.first] = input

                if (input == "stop")
                {
                    player.sendMessage("${CC.RED}${Constants.X_SYMBOL}")
                    aborted()
                    return@acceptInput
                }

                if (pendingPrompts.isNotEmpty())
                {
                    player.sendMessage("${CC.GREEN}${Constants.CHECK_SYMBOL}")
                    Tasks.delayed(1L, ::startChain)
                } else
                {
                    player.sendMessage("${CC.GREEN}${Constants.CHECK_SYMBOL} Complete!")
                    responded(responses)
                }
            }
            .start(player)
    }

    fun subscribe(
        responded: (Map<String, String>) -> Unit,
        aborted: () -> Unit
    )
    {
        this.responded = responded
        this.aborted = aborted

        startChain()
    }
}

fun Player.collectResponses(dsl: CollectResponsesPrompt.() -> Unit): CollectResponsesPrompt
{
    val prompt = CollectResponsesPrompt(this)
    prompt.dsl()
    return prompt
}
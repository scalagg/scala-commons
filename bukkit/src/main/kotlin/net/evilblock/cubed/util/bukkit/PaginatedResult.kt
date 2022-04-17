package net.evilblock.cubed.util.bukkit

import net.evilblock.cubed.util.CC
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class PaginatedResult<T>
@JvmOverloads
constructor(private val resultsPerPage: Int = 9)
{
    init
    {
        assert(resultsPerPage > 0)
    }

    fun display(sender: CommandSender, results: Collection<T>, page: Int)
    {
        this.display(sender, ArrayList(results), page)
    }

    fun display(
        sender: CommandSender, results: List<T>, page: Int, command: String? = null
    )
    {
        if (results.isEmpty())
        {
            sender.sendMessage(ChatColor.RED.toString() + "No entries were found.")
            return
        }

        val maxPages = results.size / this.resultsPerPage + 1

        if (page <= 0 || page > maxPages)
        {
            sender.sendMessage(ChatColor.RED.toString() + "Page '" + page + "' was not found. (" + ChatColor.YELLOW + "1 - " + maxPages + ChatColor.RED + ")")
            return
        }

        sender.sendMessage(this.getHeader(page, maxPages))

        var i = this.resultsPerPage * (page - 1)
        while (i < this.resultsPerPage * page && i < results.size)
        {
            sender.sendMessage(this.format(results[i], i))
            ++i
        }

        val left = "${Constants.DOUBLE_ARROW_LEFT}${Constants.DOUBLE_ARROW_LEFT}"
        val right = "${Constants.DOUBLE_ARROW_RIGHT}${Constants.DOUBLE_ARROW_RIGHT}"

        val finalLeft = "${if (page == 1) CC.RED else CC.GREEN}$left"
        val finalRight = "${if (page == maxPages) CC.RED else CC.GREEN}$right"

        if (command != null)
        {
            if (sender is Player)
            {
                val fancyMessage = FancyMessage()

                fancyMessage
                    .withMessage(finalLeft)

                if (page != 1) {
                    fancyMessage
                        .andHoverOf(
                            "${CC.GREEN}Click to view page ${CC.YELLOW}${page - 1}${CC.GREEN}."
                        )
                        .andCommandOf(
                            ClickEvent.Action.RUN_COMMAND,
                            String.format("/${command}", page - 1)
                        )
                }

                fancyMessage.withMessage(
                    " ${CC.SEC}Page ${CC.PRI}$page ${CC.SEC}out of ${CC.PRI}$maxPages ${CC.GRAY}(${results.size} results) "
                )

                fancyMessage
                    .withMessage(finalRight)

                if (page != maxPages) {
                    fancyMessage
                        .andHoverOf(
                            "${CC.GREEN}Click to view page ${CC.YELLOW}${page + 1}${CC.GREEN}."
                        )
                        .andCommandOf(
                            ClickEvent.Action.RUN_COMMAND,
                            String.format("/${command}", page + 1)
                        )
                }

                fancyMessage.sendToPlayer(sender)
            } else
            {
                sender.sendMessage(
                    "${CC.SEC}Page ${CC.PRI}$page ${CC.SEC}out of ${CC.PRI}$maxPages ${CC.GRAY}(${results.size} ${
                        if (results.size == 1) "result" else "results"
                    })"
                )
            }
        }
    }

    abstract fun getHeader(page: Int, maxPages: Int): String
    abstract fun format(result: T, resultIndex: Int): String
}

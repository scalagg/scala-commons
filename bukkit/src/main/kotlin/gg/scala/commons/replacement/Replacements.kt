package gg.scala.commons.replacement

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.util.Color
import org.bukkit.entity.Player
import java.util.regex.Pattern

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
@Service
@IgnoreAutoScan
object Replacements
{
    private val containers =
        mutableListOf<ReplacementContainer>()

    @Configure
    fun configure()
    {
        ReplacementGlobalContainer.configure()
        ReplacementGlobalContainer.let { this.containers.add(it) }
    }

    private val pattern =
        Pattern.compile(
            "\\%((?<identifier>[a-zA-Z\\d]+)-)((?<group>[a-zA-Z\\d]+)_)(?<parameters>[^%]+)\\%"
        )

    fun apply(
        text: String,
        player: Player
    ): String
    {
        val matcher = this.pattern
            .matcher(text)

        if (!matcher.find())
        {
            return text
        }

        val builder = StringBuffer()

        while (matcher.find())
        {
            val identifier = matcher
                .group(2)

            val replacement = this.containers
                .firstNotNullOfOrNull {
                    it.getReplacements()[identifier]
                }

            if (replacement == null)
            {
                println("didn't find replacement $identifier")
                continue
            }

            val group = matcher
                .group(4)

            val lambda =
                replacement.replacements
                    .replacements[group]

            if (lambda == null)
            {
                println("didn't find group $group")
                continue
            }

            val parameters = matcher
                .group(5)

            println("params: $parameters")

            val parsed = lambda
                .invoke(player, parameters)

            matcher.appendReplacement(
                builder, parsed
            )
        }

        return Color.translate(
            matcher
                .appendTail(builder)
                .toString()
        )
    }
}

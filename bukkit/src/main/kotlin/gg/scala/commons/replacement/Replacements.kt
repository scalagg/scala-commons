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

    private val expression =
        "%(.|\\n)*?%".toPattern()

    @Configure
    fun configure()
    {
        ReplacementGlobalContainer.configure()
        ReplacementGlobalContainer.let { this.containers.add(it) }
    }

    private val pattern =
        Pattern.compile(
            "\\%s((?<identifier>[a-zA-Z0-9]+)-)((?<group>[a-zA-Z0-9]+)-)(?<parameters>[^%s%s]+)\\%s"
                .format("%", "%", "%", "%")
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
                .group("identifier")

            val replacement = this.containers
                .firstNotNullOfOrNull {
                    it.getReplacements()[identifier]
                }
                ?: continue

            val group = matcher
                .group("parameters")

            val lambda =
                replacement.replacements
                    .replacements[group]
                    ?: continue

            val parameters = matcher
                .group("parameters")

            val parsed = lambda
                .invoke(player, parameters)

            if (parsed.isEmpty())
            {
                continue
            }

            matcher.appendReplacement(
                builder, parsed
            )
        }

        return Color.translate(
            matcher.appendTail(builder)
                .toString()
        )
    }
}

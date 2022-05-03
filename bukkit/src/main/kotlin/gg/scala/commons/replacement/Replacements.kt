package gg.scala.commons.replacement

import gg.scala.commons.Patterns
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import net.evilblock.cubed.util.Color
import org.bukkit.entity.Player

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

    fun apply(
        text: String,
        player: Player
    ): String
    {
        val matcher = Patterns
            .matcher(text)

        val builder = StringBuffer()

        while (matcher.find())
        {
            val identifier = matcher
                .group(2)

            val replacement = this.containers
                .firstNotNullOfOrNull {
                    it.getReplacements()[identifier]
                } ?: continue

            val group = matcher
                .group(4)

            val lambda =
                replacement.replacements
                    .replacements[group] ?: continue

            val parameters = matcher
                .group(5)

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

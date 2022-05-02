package gg.scala.commons.replacement

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
class ReplacementBuilder
{
    companion object
    {
        @JvmStatic
        fun newBuilder(): ReplacementBuilder
        {
            return ReplacementBuilder()
        }
    }

    val replacements =
        mutableMapOf<String, (Player, String) -> String>()

    fun processStatic(
        id: String,
        processor: () -> String
    ): ReplacementBuilder
    {
        val value = processor.invoke()

        this.replacements[id] =
            { _, _ -> value }

        return this
    }

    fun processListed(
        id: String,
        processor: (Player, String) -> List<String>
    ): ReplacementBuilder
    {
        this.replacements[id] = { player, string ->
            processor.invoke(player, string).joinToString("\n")
        }
        return this
    }

    fun process(
        vararg id: String,
        processor: (Player, String) -> String
    ): ReplacementBuilder
    {
        for (identifier in id)
        {
            this.replacements[identifier] = processor
        }

        return this
    }
}

package gg.scala.commons.scheme

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.entity.Player

abstract class AbstractCompositeSchemedMenu<T : Menu>
{
    protected val pattern = mutableListOf<String>()
    private val characterMappings = mutableMapOf<Char, (Player, Int) -> Button>()

    protected var title = "Menu Scheme"
    protected var configuration: T.() -> Unit = { }

    protected var onClose: (T, Player) -> Unit = { _, _ -> }

    protected val buttons = mutableMapOf<Int, (Player, Int) -> Button>()

    protected abstract fun createMenu(player: Player): T

    fun copyOf(other: AbstractCompositeSchemedMenu<T>): AbstractCompositeSchemedMenu<T>
    {
        this.title = other.title
        this.configuration = other.configuration

        this.pattern.addAll(other.pattern)
        this.characterMappings.putAll(other.characterMappings)
        return this
    }

    fun configuration(
        configuration: T.() -> Unit
    ): AbstractCompositeSchemedMenu<T>
    {
        this.configuration = configuration
        return this
    }

    fun manuallyClosed(
        configuration: (T, Player) -> Unit
    ): AbstractCompositeSchemedMenu<T>
    {
        this.onClose = configuration
        return this
    }

    fun title(title: String): AbstractCompositeSchemedMenu<T>
    {
        this.title = title
        return this
    }

    fun pattern(
        vararg pattern: String
    ): AbstractCompositeSchemedMenu<T>
    {
        this.pattern.clear()
        this.pattern.addAll(pattern)
        return this
    }

    fun map(char: Char, button: (Player, Int) -> Button): AbstractCompositeSchemedMenu<T>
    {
        this.characterMappings[char] = button
        return this
    }

    fun compose(): AbstractCompositeSchemedMenu<T>
    {
        for ((index, row) in this.pattern.withIndex())
        {
            val items = row.toCharArray()
            val startIndex = index * 9

            for ((itemIndex, item) in items.withIndex())
            {
                val mapping = this
                    .characterMappings[item]
                    ?: throw IllegalArgumentException(
                        "Character mapping not available for char \"$item\""
                    )

                this.buttons[startIndex + itemIndex] = mapping
            }
        }

        return this
    }

    fun createAndOpen(player: Player): Menu
    {
        return this.createMenu(player)
            .apply {
                openMenu(player)
            }
    }

    operator fun set(
        char: Char,
        button: (Player, Int) -> Button
    )
    {
        this.map(char, button)
    }
}

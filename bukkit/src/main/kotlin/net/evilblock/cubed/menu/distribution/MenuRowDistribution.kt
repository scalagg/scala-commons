package net.evilblock.cubed.menu.distribution

import net.evilblock.cubed.menu.Button

/**
 * Easy utility containing
 * several row item distribution mappings.
 *
 * @author GrowlyX
 * @since 3/15/2022
 */
enum class MenuRowDistribution(
    private val rowDistribution: (Int) -> List<Int>
)
{
    NONE({
        listOf()
    }),
    EVENLY({
        when (it)
        {
            0 -> listOf()
            1 -> listOf(4)
            2 -> listOf(3, 5)
            3 -> listOf(2, 4, 6)
            4 -> listOf(1, 3, 5, 7)
            5 -> listOf(0, 2, 4, 6, 8)
            6 -> listOf(1, 2, 3, 5, 6, 7)
            7 -> listOf(1, 2, 3, 4, 5, 6, 7)
            else -> listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        }
    }),
    SQUASHED({
        when (it)
        {
            0 -> listOf()
            1 -> listOf(4)
            2 -> listOf(3, 5)
            3 -> listOf(3, 4, 5)
            4 -> listOf(2, 3, 5, 6)
            5 -> listOf(2, 3, 4, 5, 6)
            6 -> listOf(1, 2, 3, 5, 6, 7)
            7 -> listOf(1, 2, 3, 4, 5, 6, 7)
            else -> listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        }
    }),
    BORDERED({
        when (it)
        {
            0 -> listOf()
            1 -> listOf(0)
            2 -> listOf(0, 8)
            3 -> listOf(0, 1, 8)
            4 -> listOf(0, 1, 7, 8)
            5 -> listOf(0, 1, 2, 7, 8)
            6 -> listOf(0, 1, 2, 6, 7, 8)
            7 -> listOf(0, 1, 2, 3, 6, 7, 8)
            else -> listOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
        }
    });

    fun allDistributed(
        buttons: Map<Int, Button>
    ): List<Int>
    {
        return mutableListOf<Int>()
            .apply {
                for ((index, values) in buttons.entries.chunked(9).withIndex())
                {
                    val buttons = mutableMapOf<Int, Button>()
                    values.forEach { entry -> buttons[entry.key] = entry.value }

                    this.addAll(
                        distributed(index + 1, buttons)
                    )
                }
            }
    }

    private fun distributed(
        row: Int, buttons: Map<Int, Button>
    ): List<Int>
    {
        val buttonsInRow = buttons.keys
            .filter {
                // math to calculate whether this button
                // position is in the correct "row".
                row * 9 >= it && (row * 9) + 9 <= it
            }.size

        return this.rowDistribution
            .invoke(buttonsInRow)
            .map { it + row }
    }
}

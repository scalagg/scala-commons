package net.evilblock.cubed.menu.pagination

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

abstract class CriteriaPaginatedMenu<T> : PaginatedMenu() {

    protected var selectedFilter: PaginationFilter<T> = NoFilter()
    protected var currentFilters: MutableSet<PaginationFilter<T>> = hashSetOf()

    protected var selectedSort: PaginationSort<T> = DefaultSort()
    protected var reversed: Boolean = false

    abstract fun getSourceSet(player: Player): Collection<T>

    abstract fun getFilters(player: Player): List<PaginationFilter<T>>

    abstract fun getSorts(player: Player): List<PaginationSort<T>>

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also { buttons ->

            val originalSet = getSourceSet(player)
            var filteredSet = arrayListOf<T>()

            if (currentFilters.isEmpty()) {
                filteredSet = ArrayList(originalSet)
            } else {
                for (filter in currentFilters) {
                    filteredSet.addAll(filter.apply(player, this, originalSet).filter { !filteredSet.contains(it) })
                }
            }

            filteredSet = ArrayList(selectedSort.apply(player, this, filteredSet))
            if (reversed) {
                filteredSet.reverse()
            }

            for (item in filteredSet) {
                buttons[buttons.size] = createItemButton(player, item)
            }
        }
    }

    open fun getResults(player: Player): Collection<T> {
        var results = getSourceSet(player)
        for (filter in currentFilters.sortedBy { it.priority() }) {
            results = filter.apply(player, this, results)
        }

        return results
    }

    abstract inner class FilteredItemButton<T>(val item: T) : Button()

    abstract fun createItemButton(player: Player, item: T): FilteredItemButton<T>

    open class NoFilter<T> : PaginationFilter<T> {
        override fun name(): String {
            return "No Filter"
        }

        override fun priority(): Int {
            return 0
        }

        override fun apply(player: Player, menu: CriteriaPaginatedMenu<T>, list: Collection<T>): Collection<T> {
            return list
        }

        override fun equals(other: Any?): Boolean {
            return other is NoFilter<*>
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    open class DefaultSort<T> : PaginationSort<T> {
        override fun name(): String {
            return "Default"
        }

        override fun apply(player: Player, menu: CriteriaPaginatedMenu<T>, src: Collection<T>): Collection<T> {
            return src
        }

        override fun equals(other: Any?): Boolean {
            return other is DefaultSort<*>
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    open inner class FilterButton : Button() {
        override fun getName(player: Player): String {
            return "${CC.GREEN}${CC.BOLD}Filter"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")

                for (filter in getFilters(player)) {
                    it.add(buildString {
                        if (filter == selectedFilter) {
                            append(" ${CC.BLUE}${CC.BOLD}» ")
                        } else {
                            append("    ")
                        }

                        if (currentFilters.contains(filter)) {
                            append("${CC.GREEN}${CC.BOLD}")
                        } else {
                            append(CC.GRAY)
                        }

                        append(filter.name())
                    })
                }

                it.add("")
                it.add("${CC.GRAY}${Constants.ARROW_UP} ${CC.GREEN}Left-Click.")
                it.add("${CC.GRAY}${CC.BOLD}${Constants.DOT_SYMBOL} ${CC.GREEN}Middle-Click to toggle.")
                it.add("${CC.GRAY}${Constants.ARROW_DOWN} ${CC.GREEN}Right-Click.")
            }
        }

        override fun getMaterial(player: Player): XMaterial
        {
            return XMaterial.HOPPER
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val filters = getFilters(player)
            if (clickType.isLeftClick) {
                selectedFilter = if (selectedFilter == filters.last()) {
                    filters.first()
                } else {
                    filters[filters.indexOf(selectedFilter) + 1]
                }
            } else if (clickType.isRightClick) {
                selectedFilter = if (selectedFilter == filters.first()) {
                    filters.last()
                } else {
                    filters[filters.indexOf(selectedFilter) - 1]
                }
            } else if (clickType == ClickType.MIDDLE || clickType == ClickType.CREATIVE) {
                if (currentFilters.contains(selectedFilter)) {
                    currentFilters.remove(selectedFilter)
                } else {
                    when {
                        selectedFilter is NoFilter -> {
                            currentFilters.clear()
                        }
                        selectedFilter.isDynamic() -> {
                            player.closeInventory()

                            selectedFilter.startCreation(player) { dynamicFilter ->
                                if (dynamicFilter != null) {
                                    currentFilters.add(dynamicFilter)
                                }

                                this@CriteriaPaginatedMenu.openMenu(player)
                            }
                        }
                        else -> {
                            currentFilters.add(selectedFilter)

                            val incompatible = currentFilters.filter { !it.isCompatible(selectedFilter) }
                            currentFilters.removeAll(incompatible)
                        }
                    }
                }
            }
        }
    }

    open inner class SortButton : Button() {
        override fun getName(player: Player): String {
            return "${CC.GREEN}${CC.BOLD}Sort"
        }

        override fun getDescription(player: Player): List<String> {
            return arrayListOf<String>().also {
                it.add("")

                for (sort in getSorts(player)) {
                    val builder = StringBuilder()

                    if (sort == selectedSort) {
                        builder.append(" ${CC.BLUE}${CC.BOLD}» ${CC.GREEN}${sort.name()}")
                    } else {
                        builder.append("    ${CC.GRAY}${sort.name()}")
                    }

                    it.add(builder.toString())
                }

                it.add("")
                it.add("${CC.GRAY}${Constants.ARROW_DOWN} ${CC.GREEN}Left-Click.")
                it.add("${CC.GRAY}${Constants.ARROW_UP} ${CC.GREEN}Right-Click.")
                it.add("${CC.GRAY}${Constants.REFRESH_SYMBOL} ${CC.GREEN}Middle-Click to reverse sort.")
            }
        }

        override fun getMaterial(player: Player): XMaterial {
            return XMaterial.REDSTONE_LAMP
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            val sorts = getSorts(player)
            if (clickType == ClickType.MIDDLE || clickType == ClickType.CREATIVE) {
                reversed = !reversed
                return
            }

            if (clickType.isLeftClick) {
                selectedSort = if (selectedSort == sorts.last()) {
                    sorts.first()
                } else {
                    sorts[sorts.indexOf(selectedSort) + 1]
                }
            } else if (clickType.isRightClick) {
                selectedSort = if (selectedSort == sorts.first()) {
                    sorts.last()
                } else {
                    sorts[sorts.indexOf(selectedSort) - 1]
                }
            }
        }
    }

}

package gg.scala.commons.configurable

import com.cryptomorin.xseries.XMaterial
import com.cryptomorin.xseries.XSound
import gg.scala.commons.persist.datasync.DataSyncService
import gg.scala.commons.spatial.Bounds
import gg.scala.commons.spatial.Position
import gg.scala.commons.spatial.toPosition
import gg.scala.commons.util.next
import gg.scala.commons.util.previous
import me.lucko.helper.Schedulers
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.menus.MapEditorMenu
import net.evilblock.cubed.menu.menus.SelectColorMenu
import net.evilblock.cubed.menu.menus.SelectItemStackMenu
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.*
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import net.evilblock.cubed.util.bukkit.prompt.NumberPrompt
import net.evilblock.cubed.util.time.Duration
import net.evilblock.cubed.util.time.TimeUtil
import net.md_5.bungee.api.chat.ClickEvent
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author Subham
 * @since 4/3/25
 */
fun <T> Menu.editStringList(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> List<String>,
    setter: T.(List<String>) -> Unit
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title:"
    )
    .setLore(getter(service.cached()).toList().mapIndexed { index, item ->
        "${CC.GRAY}${index + 1}) ${CC.WHITE}$item"
    })
    .addToLore(
        "",
        "${CC.GRAY}Click to edit..."
    )
    .toButton { player, _ ->
        Button.playNeutral(player!!)

        object : TextEditorMenu(getter(service.cached()))
        {
            override fun getPrePaginatedTitle(player: Player) = "Editing $title contents..."

            override fun onClose(player: Player)
            {
                Tasks.delayed(1L) {
                    this@editStringList.openMenu(player)
                }
            }

            override fun onSave(player: Player, list: List<String>)
            {
                with(service.cached()) {
                    setter(this, list)
                    service.sync(this)
                }

                player.sendMessage("${CC.GREEN}Updated $title!")
            }
        }.openMenu(player)
    }

fun <T> Menu.editStringMap(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Map<String, String>,
    setter: T.(Map<String, String>) -> Unit
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title:"
    )
    .setLore(getter(service.cached()).toList().mapIndexed { index, item ->
        "${CC.GRAY}${index + 1}) ${CC.WHITE}${item.first}: ${item.second}"
    })
    .addToLore(
        "",
        "${CC.GRAY}Click to edit..."
    )
    .toButton { player, _ ->
        Button.playNeutral(player!!)

        object : MapEditorMenu(getter(service.cached()).toMutableMap())
        {
            override fun getPrePaginatedTitle(player: Player) = "Editing $title contents..."

            override fun onClose(player: Player)
            {
                Tasks.delayed(1L) {
                    this@editStringMap.openMenu(player)
                }
            }

            override fun onSave(player: Player, list: Map<String, String>)
            {
                with(service.cached()) {
                    setter(this, list)
                    service.sync(this)
                }

                player.sendMessage("${CC.GREEN}Updated $title!")
            }
        }.openMenu(player)
    }


fun <T> Menu.editString(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> String,
    setter: T.(String) -> Unit
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title:"
    )

    .addToLore("${CC.WHITE}${getter(service.cached())}")
    .addToLore(
        "",
        "${CC.GRAY}Click to edit..."
    )
    .toButton { player, _ ->
        Button.playNeutral(player!!)

        Tasks.delayed(2L) {
            FancyMessage()
                .withMessage("${CC.GREEN}[Click to copy the current value]")
                .andHoverOf("Click to copy!")
                .andCommandOf(
                    ClickEvent.Action.SUGGEST_COMMAND,
                    getter(service.cached())
                )
                .sendToPlayer(player)
        }

        InputPrompt()
            .withText("${CC.YELLOW}Enter a new: ${CC.WHITE}$title")
            .acceptInput { _, response ->
                with(service.cached()) {
                    setter(this, response)
                    service.sync(this)
                }

                player.sendMessage("${CC.GREEN}Updated!")
                openMenu(player)
            }
            .start(player)
    }

fun <T> Menu.editBounds(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Bounds,
    setter: T.(Bounds) -> Unit
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title:"
    )
    .addToLore("${CC.WHITE}${getter(service.cached())}")
    .addToLore(
        "",
        "${CC.GRAY}Click to edit..."
    )
    .toButton { player, _ ->
        Button.playNeutral(player!!)
        player.closeInventory()

        InputPrompt()
            .withText("${CC.YELLOW}Type anything when you are at the bottom corner.")
            .acceptInput { _, _ ->
                val bottomCorner = player.location.toPosition()

                player.sendMessage("${CC.GREEN}${Constants.CHECK_SYMBOL} Captured the bottom corner!")
                player.playSound(player.location, XSound.ENTITY_EXPERIENCE_ORB_PICKUP.get()!!, 1.0f, 1.0f)

                Schedulers
                    .sync()
                    .runLater({
                        InputPrompt()
                            .withText("${CC.YELLOW}Type anything when you are at the top corner.")
                            .acceptInput { _, _ ->
                                val topCorner = player.location.toPosition()

                                player.sendMessage("${CC.GREEN}Captured the top corner!")
                                player.playSound(player.location, XSound.ENTITY_EXPERIENCE_ORB_PICKUP.get()!!, 1.0f, 1.0f)

                                with(service.cached()) {
                                    setter(this, Bounds(
                                        lowerLeft = bottomCorner,
                                        upperRight = topCorner
                                    )
                                    )
                                    service.sync(this)
                                }

                                player.sendMessage("${CC.GREEN}Updated!")
                                openMenu(player)
                            }
                            .start(player)
                    }, 2L)
            }
            .start(player)
    }

fun <T> Menu.editPosition(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Position,
    setter: T.(Position) -> Unit
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title:"
    )
    .addToLore("${CC.WHITE}${getter(service.cached())}")
    .addToLore(
        "",
        "${CC.GRAY}Click to edit..."
    )
    .toButton { player, _ ->
        Button.playNeutral(player!!)
        player.closeInventory()

        InputPrompt()
            .withText("${CC.YELLOW}Enter \"confirm\" in chat to set the position for: ${CC.WHITE}$title")
            .acceptInput { _, response ->
                if (response != "confirm")
                {
                    player.sendMessage("${CC.RED}Aborted.")
                    openMenu(player)
                    return@acceptInput
                }

                with(service.cached()) {
                    setter(this, player.location.toPosition())
                    service.sync(this)
                }

                player.sendMessage("${CC.GREEN}Updated!")
                openMenu(player)
            }
            .start(player)
    }

fun <T> Menu.editBoolean(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Boolean,
    setter: T.(Boolean) -> Unit
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title: ${CC.WHITE}${if (getter(service.cached())) "${CC.GREEN}Yes" else "${CC.RED}No"}"
    )
    .addToLore("${CC.GRAY}Click to edit...")
    .toButton { player, _ ->
        Button.playNeutral(player!!)

        with(service.cached()) {
            setter(this, !getter())
            service.sync(this)
        }

        openMenu(player)
    }

fun <T> Menu.editFloat(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Float,
    setter: T.(Float) -> Unit,
    range: ClosedRange<Float> = 0.0F..1.0F,
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title: ${CC.WHITE}${getter(service.cached())}"
    )
    .addToLore("${CC.GRAY}Click to edit...")
    .toButton { player, _ ->
        player!!.closeInventory()
        Button.playNeutral(player)

        NumberPrompt()
            .withText("${CC.YELLOW}Enter a new: ${CC.WHITE}$title ${CC.GRAY}(Between ${range.start}-${range.endInclusive})")
            .acceptInput {
                with(service.cached()) {
                    setter(it.toFloat().coerceIn(range))
                    service.sync(this)
                }

                openMenu(player)
            }
            .start(player)
    }


fun <T> Menu.editDouble(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Double,
    setter: T.(Double) -> Unit,
    range: ClosedRange<Double> = 0.0..1.0,
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title: ${CC.WHITE}${getter(service.cached())}"
    )
    .addToLore("${CC.GRAY}Click to edit...")
    .toButton { player, _ ->
        player!!.closeInventory()
        Button.playNeutral(player)

        NumberPrompt()
            .withText("${CC.YELLOW}Enter a new: ${CC.WHITE}$title ${CC.GRAY}(Between ${range.start}-${range.endInclusive})")
            .acceptInput {
                with(service.cached()) {
                    setter(it.toDouble().coerceIn(range))
                    service.sync(this)
                }

                openMenu(player)
            }
            .start(player)
    }

fun <T> Menu.editInt(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Int,
    setter: T.(Int) -> Unit,
    range: ClosedRange<Int> = 0..Int.MAX_VALUE,
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title: ${CC.WHITE}${getter(service.cached())}"
    )
    .addToLore("${CC.GRAY}Click to edit...")
    .toButton { player, _ ->
        player!!.closeInventory()
        Button.playNeutral(player)

        NumberPrompt()
            .withText("${CC.YELLOW}Enter a new: ${CC.WHITE}$title ${CC.GRAY}(Between ${range.start}-${range.endInclusive})")
            .acceptInput {
                with(service.cached()) {
                    setter(it.toInt().coerceIn(range))
                    service.sync(this)
                }

                openMenu(player)
            }
            .start(player)
    }

fun <T> Menu.editItemStack(
    service: DataSyncService<T>,
    title: String,
    getter: T.() -> ItemStack,
    setter: T.(ItemStack) -> Unit,
) = ItemBuilder
    .copyOf(getter(service.cached()).clone())
    .name("${CC.YELLOW}$title")
    .addToLore("${CC.GRAY}Click to edit...")
    .toButton { player, _ ->
        player!!.closeInventory()
        Button.playNeutral(player)

        SelectItemStackMenu {
            with(service.cached()) {
                setter(it.clone())
                service.sync(this)
            }

            openMenu(player)
        }.openMenu(player)
    }

inline fun <reified T : Enum<T>, U> Menu.editEnum(
    service: DataSyncService<U>,
    title: String,
    material: XMaterial,
    crossinline getter: U.() -> Enum<T>,
    crossinline setter: U.(Enum<T>) -> Unit,
) = ItemBuilder
    .of(material)
    .name("${CC.YELLOW}$title")
    .apply {
        val values = enumValues<T>()
        val current = getter(service.cached())

        values.forEach {
            addToLore(
                " ${if (current == it) CC.GREEN else CC.GRAY}${Constants.TRIANGLE_ARROW_RIGHT} ${CC.WHITE}${it.name}"
            )
        }
    }
    .addToLore(
        "",
        "${CC.GREEN}Left click to scroll down!",
        "${CC.GREEN}Right click to scroll up!",
    )
    .toButton { player, type ->
        Button.playNeutral(player!!)

        if (type!!.isRightClick)
        {
            service.editAndSave { setter(service.cached(), getter(service.cached()).previous()) }
            openMenu(player)
            return@toButton
        }

        service.editAndSave { setter(service.cached(), getter(service.cached()).next()) }
        openMenu(player)
    }

inline fun <T, U> Menu.editListSelector(
    service: DataSyncService<U>,
    title: String,
    material: XMaterial,
    crossinline getter: U.() -> T,
    crossinline values: U.() -> List<T>,
    crossinline format: T.() -> String,
    crossinline setter: U.(T) -> Unit,
) = ItemBuilder
    .of(material)
    .name("${CC.YELLOW}$title")
    .apply {
        val current = getter(service.cached())

        values(service.cached()).forEach {
            addToLore(
                " ${if (current == it) CC.GREEN else CC.GRAY}${Constants.TRIANGLE_ARROW_RIGHT} ${CC.WHITE}${format(it)}"
            )
        }
    }
    .addToLore(
        "",
        "${CC.GREEN}Left click to scroll down!",
        "${CC.GREEN}Right click to scroll up!",
    )
    .toButton { player, type ->
        Button.playNeutral(player!!)
        val cached = service.cached()

        if (type!!.isRightClick)
        {
            service.editAndSave { setter(cached, with(values(cached)) {
                getOrNull(indexOf(getter(cached)) - 1) ?: last()
            }) }
            openMenu(player)
            return@toButton
        }

        service.editAndSave { setter(cached, with(values(cached)) {
            getOrNull(indexOf(getter(cached)) + 1) ?: first()
        }) }
        openMenu(player)
    }


fun <T> Menu.editChatColor(
    service: DataSyncService<T>,
    title: String,
    getter: T.() -> ChatColor,
    setter: T.(ChatColor) -> Unit,
) = ItemBuilder
    .of(XMaterial.WHITE_WOOL)
    .data(ColorUtil
        .toWoolData(getter(service.cached()))
        .toShort())
    .name("${CC.YELLOW}$title")
    .addToLore(
        "${CC.WHITE}Value: ${getter(service.cached())}${getter(service.cached()).name}",
        "",
        "${CC.GRAY}Click to edit..."
    )
    .toButton { player, _ ->
        player!!.closeInventory()
        Button.playNeutral(player)

        SelectColorMenu {
            val chatColor = ColorUtil.toChatColor(it)
            with(service.cached()) {
                setter(chatColor)
                service.sync(this)
            }

            openMenu(player)
        }.openMenu(player)
    }

fun <T> Menu.editDuration(
    service: DataSyncService<T>,
    title: String,
    material: XMaterial,
    getter: T.() -> Long,
    setter: T.(Long) -> Unit,
    range: ClosedRange<Long> = 0L..java.time.Duration.ofDays(10L).toMillis()
) = ItemBuilder
    .of(material)
    .name(
        "${CC.YELLOW}$title: ${CC.WHITE}${getter(service.cached())}"
    )
    .addToLore("${CC.GRAY}Click to edit...")
    .toButton { player, _ ->
        player!!.closeInventory()
        Button.playNeutral(player)

        InputPrompt()
            .withText("${CC.YELLOW}Enter a new: ${CC.WHITE}$title ${CC.GRAY}(Between ${
                TimeUtil.formatMillisIntoAbbreviatedString(range.start)
            }-${
                TimeUtil.formatMillisIntoAbbreviatedString(range.endInclusive)
            })")
            .acceptInput { _, text ->
                val duration = runCatching {
                    Duration.parse(text)
                }.getOrNull()
                    ?: run {
                        player.sendMessage("${CC.RED}You entered an invalid duration!")
                        openMenu(player)
                        return@acceptInput
                    }

                with(service.cached()) {
                    setter(duration.get().coerceIn(range))
                    service.sync(this)
                }

                openMenu(player)
            }
            .start(player)
    }


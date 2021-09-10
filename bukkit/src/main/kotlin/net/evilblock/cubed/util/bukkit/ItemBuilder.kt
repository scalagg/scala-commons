/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.enchantment.GlowEnchantment
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import java.util.function.BiConsumer
import java.util.stream.Collectors

class ItemBuilder(private val item: ItemStack) {

    constructor(material: Material): this(ItemStack(material))
    constructor(material: Material, amount: Int): this(ItemStack(material, amount))
    constructor(material: XMaterial): this(material.parseItem()!!)
    constructor(material: XMaterial, amount: Int): this(material.parseItem()!!) {
        amount(amount)
    }

    fun amount(amount: Int): ItemBuilder {
        item.amount = amount
        return this
    }

    fun data(data: Short): ItemBuilder {
        item.durability = data
        return this
    }

    fun enchant(enchantment: Enchantment, level: Int): ItemBuilder {
        item.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun unenchant(enchantment: Enchantment): ItemBuilder {
        item.removeEnchantment(enchantment)
        return this
    }

    fun name(displayName: String?): ItemBuilder {
        val meta = item.itemMeta
        meta.displayName = if (displayName == null) null else ChatColor.translateAlternateColorCodes('&', displayName)
        item.itemMeta = meta
        return this
    }

    fun addToLore(vararg parts: String): ItemBuilder {
        var meta = item.itemMeta
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.type)
        }

        var lore: MutableList<String>? = meta!!.lore
        if (lore == null) {
            lore = arrayListOf()
        }

        lore.addAll(Arrays.stream(parts).map { part -> ChatColor.translateAlternateColorCodes('&', part) }.collect(Collectors.toList()))

        meta.lore = lore
        item.itemMeta = meta
        return this
    }

    fun addToLore(parts: ArrayList<String>): ItemBuilder {
        var meta = item.itemMeta
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(item.type)
        }

        var lore: MutableList<String>? = meta!!.lore
        if (lore == null) {
            lore = arrayListOf()
        }

        lore.addAll(parts.stream().map { part -> ChatColor.translateAlternateColorCodes('&', part) }.collect(Collectors.toList()))

        meta.lore = lore
        item.itemMeta = meta
        return this
    }

    fun setLore(l: Collection<String>): ItemBuilder {
        val lore = ArrayList<String>()
        val meta = item.getItemMeta()
        lore.addAll(l.stream().map { part -> ChatColor.translateAlternateColorCodes('&', part) }.collect(Collectors.toList()))
        meta.lore = lore
        item.itemMeta = meta
        return this
    }

    fun color(color: Color): ItemBuilder {
        val meta = item.itemMeta as? LeatherArmorMeta ?: throw UnsupportedOperationException("Cannot set color of a non-leather armor item.")
        meta.color = color
        item.itemMeta = meta
        return this
    }

    fun owner(owner: String): ItemBuilder {
        val meta = item.itemMeta as? SkullMeta ?: throw UnsupportedOperationException("Cannot set owner of a non-skull item.")
        meta.owner = owner
        item.itemMeta = meta
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        val meta = item.itemMeta
        meta.spigot().isUnbreakable = unbreakable
        item.itemMeta = meta
        return this
    }

    fun addFlags(vararg flags: ItemFlag): ItemBuilder {
        val meta = item.itemMeta
        meta.addItemFlags(*flags)
        item.itemMeta = meta
        return this
    }

    fun glow(): ItemBuilder {
        GlowEnchantment.addGlow(item)
        return this
    }

    fun build(): ItemStack {
        return item.clone()
    }

    fun toButton(): Button {
        return Button.placeholder(item)
    }

    fun toButton(consumer: BiConsumer<Player?, ClickType?>): Button {
        return object : Button() {
            override fun getButtonItem(player: Player): ItemStack {
                return this@ItemBuilder.build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
                consumer.accept(player, clickType)
            }
        }
    }

//    fun toUpdatingButton(consumer: BiConsumer<Player?, ClickType?>): Button {
//        return object : Button() {
//            override fun getButtonItem(player: Player): ItemStack {
//                return this@ItemBuilder.build()
//            }
//        }
//    }

    companion object {
        @JvmStatic
        fun of (material: Material): ItemBuilder {
            return ItemBuilder(material, 1)
        }

        @JvmStatic
        fun of (material: Material, amount: Int): ItemBuilder {
            return ItemBuilder(material, amount)
        }

        @JvmStatic
        fun of (material: XMaterial): ItemBuilder {
            return ItemBuilder(material, 1)
        }

        @JvmStatic
        fun of (material: XMaterial, amount: Int): ItemBuilder {
            return ItemBuilder(material, amount)
        }

        @JvmStatic
        fun copyOf(builder: ItemBuilder): ItemBuilder {
            return ItemBuilder(builder.build())
        }

        @JvmStatic
        fun copyOf(item: ItemStack): ItemBuilder {
            return ItemBuilder(item.clone())
        }
    }

}
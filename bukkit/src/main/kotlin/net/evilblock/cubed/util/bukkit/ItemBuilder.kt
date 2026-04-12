

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
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionEffect
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
        meta.displayName = if (displayName == null) null else displayName.formatRichText()
        item.itemMeta = meta
        return this
    }

    fun addCustomPotionEffect(effect: PotionEffect, splash: Boolean): ItemBuilder {
        try {
            val meta: PotionMeta = item.itemMeta as PotionMeta
            meta.setMainEffect(effect.type)
            meta.addCustomEffect(effect, splash)
            item.setItemMeta(meta)
        } catch (ignored: java.lang.ClassCastException) {
        }
        return this
    }

    fun clearLore() = this.apply {
        this.setLore(listOf())
    }

    fun clearEnchants() = this.apply {
        this.item.enchantments.clear()
    }

    fun clearFlags() = this.apply {
        val meta = item.itemMeta
        meta.itemFlags.clear()
        item.itemMeta = meta
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

        lore.addAll(Arrays.stream(parts).map { part -> part.formatRichText() }.collect(Collectors.toList()))

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

        lore.addAll(parts.stream().map { part -> part.formatRichText() }.collect(Collectors.toList()))

        meta.lore = lore
        item.itemMeta = meta
        return this
    }

    fun setLore(l: Collection<String>): ItemBuilder {
        val lore = ArrayList<String>()
        val meta = item.getItemMeta()
        lore.addAll(l.stream().map { part -> part.formatRichText() }.collect(Collectors.toList()))
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
        kotlin.runCatching {
            meta.javaClass.getMethod("setUnbreakable", java.lang.Boolean.TYPE).invoke(meta, unbreakable)
        }.onFailure {
            kotlin.runCatching {
                val spigotMeta = meta.javaClass.getMethod("spigot").invoke(meta)
                spigotMeta.javaClass.getMethod("setUnbreakable", java.lang.Boolean.TYPE).invoke(spigotMeta, unbreakable)
            }
        }
        item.itemMeta = meta
        return this
    }

    fun setBase64Head(head: String): ItemBuilder {
        val rebuilt = ItemUtils.applySkullTexture(item, head)
        item.type = rebuilt.type
        item.durability = rebuilt.durability
        item.itemMeta = rebuilt.itemMeta
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

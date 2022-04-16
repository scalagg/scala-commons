/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.util.nms.NBTUtil
import org.apache.commons.lang.WordUtils
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object ItemUtils {

    private val ITEM_STACK_CLASS =
        MinecraftReflection.getNMSClass("ItemStack")
            ?: Reflection.getClassSuppressed(
                "net.minecraft.world.item.ItemStack"
            )!!

    private val CRAFT_ITEM_STACK_CLASS =
        MinecraftReflection.getCraftBukkitClass("inventory.CraftItemStack")!!

    private val AS_NMS_COPY_METHOD = Reflection
        .getMethodSuppressed(
            CRAFT_ITEM_STACK_CLASS,
            "asNMSCopy", ItemStack::class.java
        )!!

    private val AS_BUKKIT_COPY_METHOD = Reflection
        .getMethodSuppressed(
            CRAFT_ITEM_STACK_CLASS,
            "asBukkitCopy", ITEM_STACK_CLASS
        )!!

    private val EQUIPMENT_ITEMS =
        mutableMapOf<XMaterial, EquipmentSlot>()
            .apply {
                this[XMaterial.LEATHER_HELMET] = EquipmentSlot.HEAD
                this[XMaterial.LEATHER_CHESTPLATE] = EquipmentSlot.CHEST
                this[XMaterial.LEATHER_LEGGINGS] = EquipmentSlot.LEGS
                this[XMaterial.LEATHER_BOOTS] = EquipmentSlot.FEET

                this[XMaterial.IRON_HELMET] = EquipmentSlot.HEAD
                this[XMaterial.IRON_CHESTPLATE] = EquipmentSlot.CHEST
                this[XMaterial.IRON_LEGGINGS] = EquipmentSlot.LEGS
                this[XMaterial.IRON_BOOTS] = EquipmentSlot.FEET

                this[XMaterial.GOLDEN_HELMET] = EquipmentSlot.HEAD
                this[XMaterial.GOLDEN_CHESTPLATE] = EquipmentSlot.CHEST
                this[XMaterial.GOLDEN_LEGGINGS] = EquipmentSlot.LEGS
                this[XMaterial.GOLDEN_BOOTS] = EquipmentSlot.FEET

                this[XMaterial.DIAMOND_HELMET] = EquipmentSlot.HEAD
                this[XMaterial.DIAMOND_CHESTPLATE] = EquipmentSlot.CHEST
                this[XMaterial.DIAMOND_LEGGINGS] = EquipmentSlot.LEGS
                this[XMaterial.DIAMOND_BOOTS] = EquipmentSlot.FEET

                this[XMaterial.PUMPKIN] = EquipmentSlot.HEAD
            }

    private val ARMOR_EQUIPMENT_SLOTS = listOf(
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    )

    private val SWORDS = listOf(
        XMaterial.WOODEN_SWORD,
        XMaterial.GOLDEN_SWORD,
        XMaterial.STONE_SWORD,
        XMaterial.IRON_SWORD,
        XMaterial.DIAMOND_SWORD
    )

    @JvmStatic
    fun getNmsCopy(item: ItemStack): Any {
        return AS_NMS_COPY_METHOD.invoke(null, item)
    }

    @JvmStatic
    fun getBukkitCopy(item: Any): ItemStack {
        return AS_BUKKIT_COPY_METHOD.invoke(null, item) as ItemStack
    }

    @JvmStatic
    fun getName(item: ItemStack): String {
        var name = Reflection.callMethod(getNmsCopy(item), "getName") as String

        if (name.contains(".")) {
            name = WordUtils.capitalize(item.type.toString().toLowerCase().replace("_", " "))
        }

        return name
    }

    @JvmStatic
    fun getDisplayName(itemStack: ItemStack): String {
        return if (itemStack.hasItemMeta() && itemStack.itemMeta.hasDisplayName()) {
            itemStack.itemMeta.displayName
        } else {
            getName(itemStack)
        }
    }

    @JvmStatic
    fun getChatName(itemStack: ItemStack): String {
        return if (itemStack.hasItemMeta() && itemStack.itemMeta.hasDisplayName()) {
            ChatColor.stripColor(itemStack.itemMeta.displayName)
        } else {
            getName(itemStack)
        }
    }

    @JvmStatic
    fun setMonsterEggType(item: ItemStack, type: EntityType): ItemStack {
        val idTag = NBTUtil.newTag()
        NBTUtil.setString(idTag, "id", type.getName())

        val nmsCopy = getNmsCopy(item)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)
        NBTUtil.set(tag, "EntityTag", idTag)
        NBTUtil.setTag(nmsCopy, tag)

        return getBukkitCopy(nmsCopy)
    }

    @JvmStatic
    fun makeTexturedSkull(texture: String): ItemStack {
        return applySkullTexture(ItemStack(Material.SKULL_ITEM, 1, 3), texture)
    }

    @JvmStatic
    fun applySkullTexture(itemStack: ItemStack, texture: String): ItemStack {
        val nmsCopy = getNmsCopy(itemStack)

        val tag = NBTUtil.getOrCreateTag(nmsCopy)

        val skullOwner = NBTUtil.getCompound(tag, "SkullOwner")
        NBTUtil.setString(skullOwner, "Id", UUID.randomUUID().toString())

        val textureValueTag = NBTUtil.newTag()
        NBTUtil.setString(textureValueTag, "Value", texture)

        val textures = NBTUtil.newList()
        NBTUtil.addToList(textures, textureValueTag)

        val properties = NBTUtil.getCompound(skullOwner, "Properties")
        NBTUtil.set(properties, "textures", textures)
        NBTUtil.set(skullOwner, "Properties", properties)
        NBTUtil.set(tag, "SkullOwner", skullOwner)

        NBTUtil.setTag(nmsCopy, tag)

        return getBukkitCopy(nmsCopy)
    }

    @JvmStatic
    fun isSimilar(first: ItemStack, second: ItemStack): Boolean {
        if (first.type != second.type) {
            return false
        }

        if (first.durability != second.durability) {
            return false
        }

        if (first.hasItemMeta() != second.hasItemMeta()) {
            return false
        }

        if (first.itemMeta.hasDisplayName() != second.itemMeta.hasDisplayName()) {
            return false
        }

        if (first.itemMeta.hasDisplayName()) {
            if (first.itemMeta.displayName != second.itemMeta.displayName) {
                return false
            }
        }

        if (first.itemMeta.hasLore() != second.itemMeta.hasLore()) {
            return false
        }

        return true
    }

    @JvmStatic
    fun hasSameLore(first: ItemStack, second: ItemStack): Boolean {
        if (first.hasItemMeta() != second.hasItemMeta()) {
            return false
        }

        if (!first.hasItemMeta()) {
            return true
        }

        if (first.enchantments.isEmpty() && second.enchantments.isEmpty()) {
            return true
        }

        val firstLore = first.itemMeta.lore!!
        val secondLore = second.itemMeta.lore!!

        if (firstLore.isEmpty() && secondLore.isEmpty()) {
            return true
        }

        if (firstLore.size != secondLore.size) {
            return false
        }

        for (i in firstLore.indices) {
            if (firstLore[i] != secondLore[i]) {
                return false
            }
        }

        return true
    }

    @JvmStatic
    fun hasSameEnchantments(first: ItemStack, second: ItemStack): Boolean {
        if (first.enchantments.isEmpty() && second.enchantments.isEmpty()) {
            return true
        }

        if (first.enchantments.size != second.enchantments.size) {
            return false
        }

        for (enchantment in first.enchantments) {
            if (!second.containsEnchantment(enchantment.key)) {
                return false
            }

            if (enchantment.value != second.getEnchantmentLevel(enchantment.key)) {
                return false
            }
        }

        return true
    }

    @JvmStatic
    fun itemTagHasKey(itemStack: ItemStack, key: String): Boolean {
        val nmsCopy = getNmsCopy(itemStack)

        if (!NBTUtil.hasTag(nmsCopy)) {
            return false
        }

        val tag = NBTUtil.getOrCreateTag(nmsCopy)
        return NBTUtil.hasKey(tag, key)
    }

    @JvmStatic
    fun readItemTagKey(itemStack: ItemStack, key: String): Any {
        val nmsCopy = getNmsCopy(itemStack)

        if (!NBTUtil.hasTag(nmsCopy)) {
            return false
        }

        val tag = NBTUtil.getOrCreateTag(nmsCopy)
        return NBTUtil.get(tag, key)
    }

    @JvmStatic
    fun addToItemTag(itemStack: ItemStack, key: String, value: String, preserve: Boolean = false): ItemStack {
        val nmsCopy = getNmsCopy(itemStack)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)

        NBTUtil.setString(tag, key, value)

        if (preserve) {
            preserveItemNBT(nmsCopy, tag, key)
        }

        NBTUtil.setTag(nmsCopy, tag)

        return getBukkitCopy(nmsCopy)
    }

    @JvmStatic
    fun addToItemTag(itemStack: ItemStack, pairs: List<Pair<String, String>>, preserve: Boolean = false): ItemStack {
        val nmsCopy = getNmsCopy(itemStack)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)

        for (pair in pairs) {
            NBTUtil.setString(tag, pair.first, pair.second)

            if (preserve) {
                preserveItemNBT(nmsCopy, tag, pair.first)
            }
        }

        NBTUtil.setTag(nmsCopy, tag)

        return getBukkitCopy(nmsCopy)
    }

//    @JvmStatic
//    fun addUUIDToItemTag(itemStack: ItemStack, key: String, uuid: UUID, preserve: Boolean = false): ItemStack {
//        val nmsCopy = getNmsCopy(itemStack)
//        val tag = NBTUtil.getOrCreateTag(nmsCopy)
//
//        NBTUtil.setUUID(tag, key, uuid)
//
//        if (preserve) {
//            preserveItemNBT(nmsCopy, tag, "${key}Most")
//            preserveItemNBT(nmsCopy, tag, "${key}Least")
//        }
//
//        NBTUtil.setTag(nmsCopy, tag)
//
//        return getBukkitCopy(nmsCopy)
//    }

    @JvmStatic
    fun preserveItemNBT(itemStack: ItemStack, key: String): ItemStack {
        val nmsCopy = getNmsCopy(itemStack)
        val tag = NBTUtil.getOrCreateTag(nmsCopy)

        preserveItemNBT(nmsCopy, tag, key)

        return getBukkitCopy(nmsCopy)
    }

    @JvmStatic
    fun preserveItemNBT(item: Any, tag: Any, key: String) {
        if (NBTUtil.hasKey(tag, "PreserveNBT")) {
            val currentKeys = NBTUtil.getString(tag, "PreserveNBT").split(",").toHashSet()
            currentKeys.add(key)

            NBTUtil.setString(tag, "PreserveNBT", currentKeys.joinToString(separator = ","))
        } else {
            NBTUtil.setString(tag, "PreserveNBT", key)
        }

        NBTUtil.setTag(item, tag)
    }

    /**
     * Gets a player head item with the skin of the given [owner].
     */
    @JvmStatic
    fun getPlayerHeadItem(owner: String): ItemStack {
        val itemStack = ItemStack(
            XMaterial.SKELETON_SKULL.parseMaterial(), 1, 3
        )
        val itemMeta = itemStack.itemMeta as SkullMeta

        itemMeta.owner = owner
        itemStack.itemMeta = itemMeta

        return itemStack
    }

    private val LEATHER_ARMOR_TYPES = listOf(
        XMaterial.LEATHER_BOOTS,
        XMaterial.LEATHER_LEGGINGS,
        XMaterial.LEATHER_CHESTPLATE,
        XMaterial.LEATHER_HELMET
    )

    /**
     * Applies the given [color] to the given [itemStack]'s metadata if the [itemStack] is a leather armor type.
     */
    @JvmStatic
    fun colorLeatherArmor(itemStack: ItemStack, color: Color): ItemStack {
        if (!LEATHER_ARMOR_TYPES.contains(
                XMaterial.matchXMaterial(itemStack.type)
        )) {
            throw IllegalArgumentException("Item stack is not of leather armor type!")
        }

        val meta = itemStack.itemMeta as LeatherArmorMeta
        meta.color = color
        itemStack.itemMeta = meta
        return itemStack
    }

    @JvmStatic
    fun getEquipmentSlot(type: Material): EquipmentSlot? {
        return EQUIPMENT_ITEMS[XMaterial.matchXMaterial(type)]
    }

    @JvmStatic
    fun isArmorEquipment(type: Material): Boolean {
        return ARMOR_EQUIPMENT_SLOTS.contains(getEquipmentSlot(type))
    }

    @JvmStatic
    fun getSwords(): List<XMaterial> {
        return SWORDS
    }

    @JvmStatic
    fun isSword(type: Material): Boolean {
        return SWORDS.contains(
            XMaterial.matchXMaterial(type)
        )
    }

    @JvmStatic
    fun isSword(item: ItemStack): Boolean {
        return SWORDS.contains(
            XMaterial.matchXMaterial(item.type)
        )
    }
}

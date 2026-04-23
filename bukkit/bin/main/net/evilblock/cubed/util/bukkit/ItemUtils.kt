/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.bukkit

import com.cryptomorin.xseries.XMaterial
import de.tr7zw.changeme.nbtapi.NBT
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.apache.commons.lang.WordUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object ItemUtils
{
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

    private val ITEM_META_CLASS = Reflection.getClassSuppressed("org.bukkit.inventory.meta.ItemMeta")
    private val NAMESPACED_KEY_CLASS = Reflection.getClassSuppressed("org.bukkit.NamespacedKey")
    private val PERSISTENT_DATA_CONTAINER_CLASS = Reflection.getClassSuppressed("org.bukkit.persistence.PersistentDataContainer")
    private val PERSISTENT_DATA_TYPE_CLASS = Reflection.getClassSuppressed("org.bukkit.persistence.PersistentDataType")
    private val GET_PERSISTENT_DATA_CONTAINER_METHOD = ITEM_META_CLASS?.let {
        Reflection.getMethodSuppressed(it, "getPersistentDataContainer")
    }
    private val PERSISTENT_DATA_HAS_METHOD = if (PERSISTENT_DATA_CONTAINER_CLASS != null && NAMESPACED_KEY_CLASS != null && PERSISTENT_DATA_TYPE_CLASS != null)
    {
        Reflection.getMethodSuppressed(
            PERSISTENT_DATA_CONTAINER_CLASS,
            "has",
            NAMESPACED_KEY_CLASS,
            PERSISTENT_DATA_TYPE_CLASS
        )
    } else null
    private val PERSISTENT_DATA_GET_METHOD = if (PERSISTENT_DATA_CONTAINER_CLASS != null && NAMESPACED_KEY_CLASS != null && PERSISTENT_DATA_TYPE_CLASS != null)
    {
        Reflection.getMethodSuppressed(
            PERSISTENT_DATA_CONTAINER_CLASS,
            "get",
            NAMESPACED_KEY_CLASS,
            PERSISTENT_DATA_TYPE_CLASS
        )
    } else null
    private val PERSISTENT_DATA_SET_METHOD = if (PERSISTENT_DATA_CONTAINER_CLASS != null && NAMESPACED_KEY_CLASS != null && PERSISTENT_DATA_TYPE_CLASS != null)
    {
        Reflection.getMethodSuppressed(
            PERSISTENT_DATA_CONTAINER_CLASS,
            "set",
            NAMESPACED_KEY_CLASS,
            PERSISTENT_DATA_TYPE_CLASS,
            Any::class.java
        )
    } else null
    private val NAMESPACED_KEY_CONSTRUCTOR = kotlin.runCatching {
        NAMESPACED_KEY_CLASS
            ?.getDeclaredConstructor(String::class.java, String::class.java)
            ?.apply { isAccessible = true }
    }.getOrNull()
    private val PERSISTENT_DATA_STRING_TYPE = kotlin.runCatching {
        PERSISTENT_DATA_TYPE_CLASS
            ?.getField("STRING")
            ?.get(null)
    }.getOrNull()

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
    fun getNmsCopy(item: ItemStack): Any
    {
        return AS_NMS_COPY_METHOD.invoke(null, item)
    }

    @JvmStatic
    fun getBukkitCopy(item: Any): ItemStack
    {
        return AS_BUKKIT_COPY_METHOD.invoke(null, item) as ItemStack
    }

    @JvmStatic
    fun getName(item: ItemStack): String
    {
        var name = Reflection.callMethod(getNmsCopy(item), "getName") as String

        if (name.contains("."))
        {
            name = WordUtils.capitalize(item.type.toString().toLowerCase().replace("_", " "))
        }

        return name
    }

    @JvmStatic
    fun getDisplayName(itemStack: ItemStack): String
    {
        return if (itemStack.hasItemMeta() && itemStack.itemMeta.hasDisplayName())
        {
            itemStack.itemMeta.displayName
        } else
        {
            getName(itemStack)
        }
    }

    @JvmStatic
    fun getChatName(itemStack: ItemStack): String
    {
        return if (itemStack.hasItemMeta() && itemStack.itemMeta.hasDisplayName())
        {
            ChatColor.stripColor(itemStack.itemMeta.displayName)
        } else
        {
            getName(itemStack)
        }
    }

    @JvmStatic
    fun setMonsterEggType(item: ItemStack, type: EntityType): ItemStack
    {
        if (ServerVersion.getVersion().isNewerThanOrEquals(ServerVersion.v1_21))
        {
            val material = Material.matchMaterial("${type.name}_SPAWN_EGG")
            if (material != null)
            {
                item.type = material
            }

            return item
        }

        NBT.modify(item) { t ->
            t.resolveOrCreateCompound("EntityTag").setString("id", type.name)
        }

        return item
    }

    @JvmStatic
    fun makeTexturedSkull(texture: String): ItemStack
    {
        val base = if (ServerVersion.getVersion().isNewerThanOrEquals(ServerVersion.v1_13))
        {
            ItemStack(XMaterial.PLAYER_HEAD.parseMaterial()!!)
        } else
        {
            ItemStack(Material.SKULL_ITEM, 1, 3)
        }

        return applySkullTexture(base, texture)
    }

    @JvmStatic
    fun applySkullTexture(itemStack: ItemStack, texture: String): ItemStack
    {
        if (ServerVersion.getVersion().isNewerThanOrEquals(ServerVersion.v1_13))
        {
            itemStack.type = XMaterial.PLAYER_HEAD.parseMaterial()!!
            itemStack.durability = 0
        }

        val skullMeta = itemStack.itemMeta as? SkullMeta
            ?: return itemStack

        if (ServerVersion.getVersion().isNewerThanOrEquals(ServerVersion.v1_21) && applyModernSkullTexture(skullMeta, texture))
        {
            itemStack.itemMeta = skullMeta
            return itemStack
        }

        val profile = buildLegacySkullProfile(texture)

        kotlin.runCatching {
            val profileField = skullMeta.javaClass.getDeclaredField("profile")
            profileField.isAccessible = true
            profileField.set(skullMeta, profile)
        }.recoverCatching {
            val profileField = skullMeta.javaClass.declaredFields.firstOrNull { field ->
                field.name.equals("profile", ignoreCase = true)
            } ?: throw it

            profileField.isAccessible = true
            profileField.set(skullMeta, profile)
        }

        itemStack.itemMeta = skullMeta
        return itemStack
    }

    private fun buildLegacySkullProfile(texture: String): Any
    {
        return MinecraftReflection.newGameProfile(UUID.randomUUID(), "scala-head", texture, "")
    }

    private fun applyModernSkullTexture(skullMeta: SkullMeta, texture: String): Boolean
    {
        val paperProfileClass = Reflection.getClassSuppressed("com.destroystokyo.paper.profile.PlayerProfile")
            ?: return false
        val profileClass = Reflection.getClassSuppressed("org.bukkit.profile.PlayerProfile")
            ?: return false

        val createProfile = Reflection.getMethodSuppressed(
            Bukkit::class.java,
            "createProfile",
            UUID::class.java,
            String::class.java
        ) ?: return false

        val playerProfile = createProfile.invoke(null, UUID.randomUUID(), "scala-head")
            ?: return false

        val profilePropertyClass = Reflection.getClassSuppressed("com.destroystokyo.paper.profile.ProfileProperty")
            ?: return false
        val property = Reflection.callConstructor(profilePropertyClass, "textures", texture)
            ?: Reflection.callConstructor(profilePropertyClass, "textures", texture, null)
            ?: return false
        val setProperty = Reflection.getMethodSuppressed(paperProfileClass, "setProperty", profilePropertyClass)
            ?: return false
        setProperty.invoke(playerProfile, property)

        val setPlayerProfile = Reflection.getMethodSuppressed(skullMeta.javaClass, "setPlayerProfile", paperProfileClass)
            ?: Reflection.getMethodSuppressed(skullMeta.javaClass, "setOwnerProfile", profileClass)
            ?: return false

        setPlayerProfile.invoke(skullMeta, playerProfile)
        return true
    }


    @JvmStatic
    fun isSimilar(first: ItemStack, second: ItemStack): Boolean
    {
        if (first.type != second.type)
        {
            return false
        }

        if (first.durability != second.durability)
        {
            return false
        }

        if (first.hasItemMeta() != second.hasItemMeta())
        {
            return false
        }

        if (first.itemMeta.hasDisplayName() != second.itemMeta.hasDisplayName())
        {
            return false
        }

        if (first.itemMeta.hasDisplayName())
        {
            if (first.itemMeta.displayName != second.itemMeta.displayName)
            {
                return false
            }
        }

        if (first.itemMeta.hasLore() != second.itemMeta.hasLore())
        {
            return false
        }

        return true
    }

    @JvmStatic
    fun hasSameLore(first: ItemStack, second: ItemStack): Boolean
    {
        if (first.hasItemMeta() != second.hasItemMeta())
        {
            return false
        }

        if (!first.hasItemMeta())
        {
            return true
        }

        if (first.enchantments.isEmpty() && second.enchantments.isEmpty())
        {
            return true
        }

        val firstLore = first.itemMeta.lore!!
        val secondLore = second.itemMeta.lore!!

        if (firstLore.isEmpty() && secondLore.isEmpty())
        {
            return true
        }

        if (firstLore.size != secondLore.size)
        {
            return false
        }

        for (i in firstLore.indices)
        {
            if (firstLore[i] != secondLore[i])
            {
                return false
            }
        }

        return true
    }

    @JvmStatic
    fun hasSameEnchantments(first: ItemStack, second: ItemStack): Boolean
    {
        if (first.enchantments.isEmpty() && second.enchantments.isEmpty())
        {
            return true
        }

        if (first.enchantments.size != second.enchantments.size)
        {
            return false
        }

        for (enchantment in first.enchantments)
        {
            if (!second.containsEnchantment(enchantment.key))
            {
                return false
            }

            if (enchantment.value != second.getEnchantmentLevel(enchantment.key))
            {
                return false
            }
        }

        return true
    }

    @JvmStatic
    fun itemTagHasKey(itemStack: ItemStack, key: String): Boolean
    {
        val nbtHasKey = kotlin.runCatching {
            NBT.readNbt(itemStack)?.hasTag(key) == true
        }.getOrDefault(false)

        if (nbtHasKey)
        {
            return true
        }

        return readPersistentString(itemStack, key) != null
    }

    @JvmStatic
    fun readItemTagKey(itemStack: ItemStack, key: String): String?
    {
        val nbtValue = kotlin.runCatching {
            NBT.readNbt(itemStack)?.getString(key)
        }.getOrNull()

        if (nbtValue != null)
        {
            return nbtValue
        }

        return readPersistentString(itemStack, key)
    }

    @JvmStatic
    fun addToItemTag(itemStack: ItemStack, key: String, value: String, preserve: Boolean = false): ItemStack
    {
        val wroteWithNbt = kotlin.runCatching {
            NBT.modify(itemStack) { t ->
                t.setString(key, value)
            }
            true
        }.getOrDefault(false)

        if (!wroteWithNbt)
        {
            writePersistentString(itemStack, key, value)
        }

        if (preserve)
        {
            preserveItemNBT(itemStack, key)
        }
        return itemStack
    }

    @JvmStatic
    fun preserveItemNBT(itemStack: ItemStack, key: String): ItemStack
    {
        val modified = kotlin.runCatching {
            NBT.modify(itemStack) { t ->
                if (t.hasTag("PreserveNBT"))
                {
                    val currentKeys = t.getString("PreserveNBT").split(",").toHashSet()
                    currentKeys.add(key)

                    t.setString("PreserveNBT", currentKeys.joinToString(","))
                } else
                {
                    t.setString("PreserveNBT", key)
                }
            }
            true
        }.getOrDefault(false)

        if (!modified)
        {
            val currentKeys = readPersistentString(itemStack, "PreserveNBT")
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?.toHashSet()
                ?: hashSetOf()

            currentKeys.add(key)
            writePersistentString(itemStack, "PreserveNBT", currentKeys.joinToString(","))
        }

        return itemStack
    }

    private fun writePersistentString(itemStack: ItemStack, key: String, value: String): Boolean
    {
        val meta = itemStack.itemMeta
            ?: return false
        val persistentDataContainer = getPersistentDataContainer(meta)
            ?: return false
        val namespacedKey = createPersistentDataKey(key)
            ?: return false
        val stringType = PERSISTENT_DATA_STRING_TYPE
            ?: return false

        return kotlin.runCatching {
            PERSISTENT_DATA_SET_METHOD?.invoke(
                persistentDataContainer,
                namespacedKey,
                stringType,
                value
            )
            itemStack.itemMeta = meta
            true
        }.getOrDefault(false)
    }

    private fun readPersistentString(itemStack: ItemStack, key: String): String?
    {
        val meta = itemStack.itemMeta
            ?: return null
        val persistentDataContainer = getPersistentDataContainer(meta)
            ?: return null
        val namespacedKey = createPersistentDataKey(key)
            ?: return null
        val stringType = PERSISTENT_DATA_STRING_TYPE
            ?: return null

        val hasKey = kotlin.runCatching {
            PERSISTENT_DATA_HAS_METHOD?.invoke(
                persistentDataContainer,
                namespacedKey,
                stringType
            ) as? Boolean
        }.getOrNull()

        if (hasKey == false)
        {
            return null
        }

        return kotlin.runCatching {
            PERSISTENT_DATA_GET_METHOD?.invoke(
                persistentDataContainer,
                namespacedKey,
                stringType
            ) as? String
        }.getOrNull()
    }

    private fun getPersistentDataContainer(meta: Any): Any?
    {
        return kotlin.runCatching {
            GET_PERSISTENT_DATA_CONTAINER_METHOD?.invoke(meta)
        }.getOrNull()
    }

    private fun createPersistentDataKey(key: String): Any?
    {
        val normalizedKey = buildString {
            key.lowercase(Locale.ROOT).forEach { character ->
                append(
                    if (character.isLetterOrDigit() || character == '/' || character == '.' || character == '_' || character == '-')
                        character else '-'
                )
            }
        }

        return kotlin.runCatching {
            NAMESPACED_KEY_CONSTRUCTOR?.newInstance("sccommons", normalizedKey)
        }.getOrNull()
    }

    /**
     * Gets a player head item with the skin of the given [owner].
     */
    @JvmStatic
    fun getPlayerHeadItem(owner: String): ItemStack
    {
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
    fun colorLeatherArmor(itemStack: ItemStack, color: Color): ItemStack
    {
        if (!LEATHER_ARMOR_TYPES.contains(
                XMaterial.matchXMaterial(itemStack.type)
            )
        )
        {
            throw IllegalArgumentException("Item stack is not of leather armor type!")
        }

        val meta = itemStack.itemMeta as LeatherArmorMeta
        meta.color = color
        itemStack.itemMeta = meta
        return itemStack
    }

    @JvmStatic
    fun getEquipmentSlot(type: Material): EquipmentSlot?
    {
        return EQUIPMENT_ITEMS[XMaterial.matchXMaterial(type)]
    }

    @JvmStatic
    fun isArmorEquipment(type: Material): Boolean
    {
        return ARMOR_EQUIPMENT_SLOTS.contains(getEquipmentSlot(type))
    }

    @JvmStatic
    fun getSwords(): List<XMaterial>
    {
        return SWORDS
    }

    @JvmStatic
    fun isSword(type: Material): Boolean
    {
        return SWORDS.contains(
            XMaterial.matchXMaterial(type)
        )
    }

    @JvmStatic
    fun isSword(item: ItemStack): Boolean
    {
        return SWORDS.contains(
            XMaterial.matchXMaterial(item.type)
        )
    }
}

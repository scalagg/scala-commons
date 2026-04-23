/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.serialize

import com.google.gson.*
import de.tr7zw.changeme.nbtapi.NBT
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.util.ServerVersion
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.potion.PotionEffect
import java.lang.reflect.Type
import kotlin.Throws

class ItemStackAdapter : JsonDeserializer<ItemStack>, JsonSerializer<ItemStack>
{

    override fun serialize(item: ItemStack, type: Type, context: JsonSerializationContext): JsonElement
    {
        return serialize(item)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): ItemStack
    {
        return deserialize(element)
    }

    companion object
    {
        @JvmStatic
        fun serialize(item: ItemStack?): JsonElement
        {
            var item = item
            if (item == null || item.type == Material.AIR)
            {
                return JsonNull.INSTANCE
            }

            val element = JsonObject().also {
                if (ServerVersion.getVersion().isNewerThanOrEquals(ServerVersion.v1_13))
                {
                    it.addProperty("material", item.type.name)
                }
                else
                {
                    it.addProperty("id", item.type.id)
                }
                it.addProperty(getDataKey(item), item.durability)
                it.addProperty("count", item.amount)
            }

            if (item.hasItemMeta())
            {
                val meta = item.itemMeta
                if (meta.hasDisplayName())
                {
                    element.addProperty("name", meta.displayName)
                }

                if (meta.hasLore())
                {
                    element.add("lore", convertStringList(meta.lore) as JsonElement)
                }

                if (meta is LeatherArmorMeta)
                {
                    element.addProperty("color", meta.color.asRGB() as Number)
                } else if (meta is SkullMeta)
                {
                    element.addProperty("skull", meta.owner)
                } else if (meta is BookMeta)
                {
                    element.addProperty("title", meta.title)
                    element.addProperty("author", meta.author)
                    element.add("pages", convertStringList(meta.pages) as JsonElement)
                } else if (meta is PotionMeta)
                {
//                    element.add("potion-base", JsonObject().also {
//                        it.addProperty("potionType", meta.basePotionData.type.name)
//                        it.addProperty("extended", meta.basePotionData.isExtended)
//                        it.addProperty("upgraded", meta.basePotionData.isUpgraded)
//                    })

                    if (meta.customEffects.isNotEmpty())
                    {
                        element.add("potion-effects", serializePotionEffectList(meta.customEffects))
                    }
                } else if (meta is MapMeta)
                {
                    element.addProperty("scaling", java.lang.Boolean.valueOf(meta.isScaling))
                } else if (meta is EnchantmentStorageMeta)
                {
                    val storedEnchantments = JsonObject()

                    for ((key, value) in meta.storedEnchants)
                    {
                        storedEnchantments.addProperty(key.name, value as Number)
                    }

                    element.add("stored-enchants", storedEnchantments as JsonElement)
                }

                val preservedFlags = arrayListOf<ItemFlag>()
                for (flag in ItemFlag.values())
                {
                    if (meta.hasItemFlag(flag))
                    {
                        preservedFlags.add(flag)
                    }
                }

                if (preservedFlags.isNotEmpty())
                {
                    element.add("preserved-flags", JsonArray().also {
                        for (flag in preservedFlags)
                        {
                            it.add(JsonPrimitive(flag.name))
                        }
                    })
                }
            }

            if (item.enchantments.isNotEmpty())
            {
                val enchantments = JsonObject()

                for (entry2 in item.enchantments.entries)
                {
                    enchantments.addProperty(entry2.key.name, entry2.value as Number)
                }

                element.add("enchants", enchantments as JsonElement)
            }

            if (ServerVersion.getVersion().isOlderThanOrEquals(ServerVersion.v1_8_8))
            {
                if (ItemUtils.itemTagHasKey(item, "PreserveNBT"))
                {
                    val readNBT = NBT.readNbt(item)
                    element.add("preserved-nbt", JsonPrimitive(readNBT.toString()))
                }
            }

            return element
        }

        @JvmStatic
        fun deserialize(json: JsonElement?): ItemStack
        {
            if (json == null || json !is JsonObject)
            {
                return ItemStack(Material.AIR)
            }

            val element = json as JsonObject?
            val material = if (element!!.has("material")) Material.matchMaterial(element.get("material").asString) else null
            val id = if (material == null) element.get("id").asInt else -1
            val data =
                (if (element.has("damage")) element.get("damage").asShort else if (element.has("data")) element.get("data").asShort else 0).toShort()
            val count = if (element.has("count")) element.get("count").asInt else 1
            val item: ItemStack = if (material != null) {
                ItemStack(material, count).also { it.durability = data }
            } else {
                val legacyName = Material.values().firstOrNull {
                    kotlin.runCatching {
                        val getId = it.javaClass.getMethod("getId")
                        (getId.invoke(it) as? Int) == id
                    }.getOrDefault(false)
                }?.name

                val modernMaterial = legacyName?.let {
                    kotlin.runCatching {
                        Material::class.java.getMethod("getMaterial", String::class.java, Boolean::class.javaPrimitiveType)
                            .invoke(null, it, true) as? Material
                    }.getOrNull()
                }

                if (modernMaterial != null) {
                    ItemStack(modernMaterial, count).also { it.durability = data }
                } else {
                    ItemStack(Material.AIR)
                }
            }

            val meta = item.itemMeta
            if (meta != null)
            {
                if (element.has("name"))
                {
                    meta.displayName = element.get("name").asString
                }

                if (element.has("lore"))
                {
                    meta.lore = convertStringList(element.get("lore"))
                }

                if (element.has("color"))
                {
                    (meta as LeatherArmorMeta).color = Color.fromRGB(element.get("color").asInt)
                } else if (element.has("skull"))
                {
                    val skullValue = element.get("skull")
                    if (!skullValue.isJsonNull)
                    {
                        (meta as SkullMeta).owner = skullValue.asString
                    }
                } else if (element.has("title"))
                {
                    (meta as BookMeta).title = element.get("title").asString
                    meta.author = element.get("author").asString
                    meta.pages = convertStringList(element.get("pages"))
                } else if (element.has("potion-base"))
                {

                    val potionMeta = meta as PotionMeta
                    // 1.12 serialization
//                val jsonPotionBase = element.get("potion-base").asJsonObject
//                potionMeta.basePotionData = PotionData(
//                    PotionType.valueOf(jsonPotionBase.get("potionType").asString),
//                    jsonPotionBase.get("extended").asBoolean,
//                    jsonPotionBase.get("upgraded").asBoolean
//                )

                    if (element.has("potion-effects"))
                    {
                        for (effect in deserializePotionEffectList(element.get("potion-effects")))
                        {
                            potionMeta.addCustomEffect(effect, false)
                        }
                    }
                } else if (element.has("scaling"))
                {
                    (meta as MapMeta).isScaling = element.get("scaling").asBoolean
                } else if (element.has("stored-enchants"))
                {
                    val enchantments = element.get("stored-enchants") as JsonObject
                    for (enchantment in Enchantment.values())
                    {
                        if (enchantments.has(enchantment.name))
                        {
                            (meta as EnchantmentStorageMeta).addStoredEnchant(
                                enchantment,
                                enchantments.get(enchantment.name).asInt,
                                true
                            )
                        }
                    }
                }

                if (element.has("preserved-flags"))
                {
                    val preservedFlags = element.get("preserved-flags").asJsonArray
                    for (flag in preservedFlags)
                    {
                        try
                        {
                            meta.addItemFlags(ItemFlag.valueOf(flag.asString))
                        } catch (e: Exception)
                        {
                            e.printStackTrace()
                            ScalaCommonsSpigot.instance.logger.warning("Failed to deserialize an ItemFlag: \"$flag\"")
                        }
                    }
                }

                item.itemMeta = meta
            }

            if (element.has("enchants"))
            {
                val enchantments = element.get("enchants") as JsonObject
                for (enchantment in Enchantment.values())
                {
                    if (enchantments.has(enchantment.name))
                    {
                        item.addUnsafeEnchantment(enchantment, enchantments.get(enchantment.name).asInt)
                    }
                }
            }

            if (element.has("preserved-nbt"))
            {
                val itemStack = NBT.itemStackFromNBT(
                    NBT.parseNBT(element.getAsJsonPrimitive("preserved-nbt").asString)
                )

                if (itemStack != null)
                {
                    return itemStack
                }
            }

            return item
        }

        private fun getDataKey(item: ItemStack): String
        {
            if (item.type === Material.AIR)
            {
                return "data"
            }

            return if (Enchantment.DURABILITY.canEnchantItem(item))
            {
                "damage"
            } else "data"
        }

        @JvmStatic
        fun convertStringList(strings: Collection<String>): JsonArray
        {
            val ret = JsonArray()

            for (string in strings)
            {
                ret.add(JsonPrimitive(string) as JsonElement)
            }

            return ret
        }

        @JvmStatic
        fun convertStringList(jsonElement: JsonElement): List<String>
        {
            val array = jsonElement.asJsonArray
            val ret = ArrayList<String>()

            for (element in array)
            {
                ret.add(element.asString)
            }

            return ret
        }

        @JvmStatic
        fun serializePotionEffectList(potionEffects: Collection<PotionEffect>): JsonArray
        {
            return JsonArray().also { array -> potionEffects.forEach { array.add(PotionEffectAdapter.toJson(it)) } }
        }

        @JvmStatic
        fun deserializePotionEffectList(json: JsonElement): List<PotionEffect>
        {
            if (!json.isJsonArray)
            {
                return emptyList()
            }

            val array = json.asJsonArray
            val effects = ArrayList<PotionEffect>()

            for (element in array)
            {
                val effect = PotionEffectAdapter.fromJson(element) ?: continue
                effects.add(effect)
            }

            return effects
        }

    }

}

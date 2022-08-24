/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.nms

import net.evilblock.cubed.util.Reflection
import java.lang.reflect.Method

object NBTUtil
{
    private fun nbtClass(name: String) = Reflection
        .getClassSuppressed("net.minecraft.nbt.$name")

    private val NBT_BASE_CLASS: Class<*> = MinecraftReflection
        .getNMSClass("NBTBase") ?: nbtClass("NBTBase")!!

    private val NBT_NUMBER_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTBase\$NBTNumber")
        ?: MinecraftReflection.getNMSClass("NBTNumber") // 1.12 class
        ?: nbtClass("NBTNumber")!! // 1.17+ class

    private val NBT_INT_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTTagInt")
        ?: nbtClass("NBTTagInt")!! // 1.17+ class

    private val NBT_LONG_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTTagLong")
        ?: nbtClass("NBTTagLong")!! // 1.17+ class

    private val NBT_DOUBLE_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTTagDouble")
        ?: nbtClass("NBTTagDouble")!! // 1.17+ class

    private val NBT_FLOAT_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTTagFloat")
        ?: nbtClass("NBTTagFloat")!! // 1.17+ class

    private val NBT_TAG_STRING_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTTagString")
        ?: nbtClass("NBTTagString")!! // 1.17+ class

    private val NBT_TAG_STRING_READ_METHOD: Method? =
        Reflection.getDeclaredMethod(NBT_TAG_STRING_CLASS, "a_")
            ?: Reflection.getDeclaredMethod(NBT_TAG_STRING_CLASS, "c_") // 1.12 class

    private val NBT_TAG_STRING_VALUE_MODERN = Reflection
        .getDeclaredField(
            NBT_TAG_STRING_CLASS, "A"
        )?.apply {
            this.isAccessible = true
        } // 1.17+ class

    private val NBT_TAG_COMPOUND_CLASS: Class<*> = MinecraftReflection
        .getNMSClass("NBTTagCompound") ?: nbtClass("NBTTagCompound")!! // 1.17+ class

    private val HAS_KEY_METHOD: Method =
        Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "hasKey", String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "b", String::class.java)!!

    private val GET_METHOD: Method = Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "get", String::class.java)
        ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "c", String::class.java)!!

    private val SET_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "set", String::class.java, NBT_BASE_CLASS)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "a", String::class.java, NBT_BASE_CLASS)!!

    private val GET_STRING_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "getString", String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "l", String::class.java)!!

    private val SET_STRING_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "setString", String::class.java, String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "a", String::class.java, String::class.java)!!

    private val GET_INT_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "getInt", String::class.java)
            ?: Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "h", String::class.java)!!

    private val SET_INT_METHOD: Method = Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "setInt", String::class.java, Int::class.java)
        ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "a", String::class.java, Int::class.java)!!

    private val GET_DOUBLE_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "getDouble", String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "k", String::class.java)!!

    private val SET_DOUBLE_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "setDouble", String::class.java, Double::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "a", String::class.java, Double::class.java)!!

    private val GET_LONG_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "getLong", String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "i", String::class.java)!!

    private val SET_LONG_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "setLong", String::class.java, Long::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "a", String::class.java, Long::class.java)!!

    private val GET_FLOAT_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "getFloat", String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "j", String::class.java)!!

    private val SET_FLOAT_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "setFloat", String::class.java, Float::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "a", String::class.java, Float::class.java)!!

    private val GET_COMPOUND_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "getCompound", String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "p", String::class.java)!!

    private val REMOVE_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_COMPOUND_CLASS, "remove", String::class.java)
            ?: Reflection.getMethodSuppressed(NBT_TAG_COMPOUND_CLASS, "r", String::class.java)!!

    private val ITEM_STACK_CLASS: Class<*> = MinecraftReflection.getNMSClass("ItemStack")
        ?: Reflection.getClassSuppressed("net.minecraft.world.item.ItemStack")!!

    private val HAS_TAG_METHOD: Method = Reflection.getDeclaredMethod(ITEM_STACK_CLASS, "hasTag")
        ?: Reflection.getDeclaredMethod(ITEM_STACK_CLASS, "t")!!

    private val GET_TAG_METHOD: Method = Reflection.getDeclaredMethod(ITEM_STACK_CLASS, "getTag")
        ?: Reflection.getDeclaredMethod(ITEM_STACK_CLASS, "v")!!

    private val SET_TAG_METHOD: Method =
        Reflection.getDeclaredMethod(ITEM_STACK_CLASS, "setTag", NBT_TAG_COMPOUND_CLASS)
            ?: Reflection.getDeclaredMethod(ITEM_STACK_CLASS, "b", NBT_TAG_COMPOUND_CLASS)!!

    private val NBT_TAG_LIST_CLASS: Class<*> = MinecraftReflection.getNMSClass("NBTTagList")
        ?: nbtClass("NBTTagList")!! // 1.17+ class

    private val NBT_TAG_LIST_ADD_METHOD: Method =
        Reflection.getDeclaredMethod(NBT_TAG_LIST_CLASS, "add", NBT_BASE_CLASS)!!

    @JvmStatic
    fun newTag(): Any
    {
        return NBT_TAG_COMPOUND_CLASS.newInstance()
    }

    @JvmStatic
    fun newList(): Any
    {
        return NBT_TAG_LIST_CLASS.newInstance()
    }

    @JvmStatic
    fun addToList(list: Any, base: Any)
    {
        NBT_TAG_LIST_ADD_METHOD.invoke(list, base)
    }

    @JvmStatic
    fun hasTag(item: Any): Boolean
    {
        return HAS_TAG_METHOD.invoke(item) as Boolean
    }

    @JvmStatic
    fun getOrCreateTag(item: Any): Any
    {
        return if (hasTag(item))
        {
            GET_TAG_METHOD.invoke(item)
        } else
        {
            newTag()
        }
    }

    @JvmStatic
    fun setTag(item: Any, tag: Any)
    {
        SET_TAG_METHOD(item, tag)
    }

    @JvmStatic
    fun hasKey(tag: Any, key: String): Boolean
    {
        return HAS_KEY_METHOD.invoke(tag, key) as Boolean
    }

    @JvmStatic
    fun get(tag: Any, key: String): Any
    {
        return GET_METHOD.invoke(tag, key)
    }

    @JvmStatic
    fun set(tag: Any, key: String, value: Any)
    {
        SET_METHOD.invoke(tag, key, value)
    }

    @JvmStatic
    fun remove(tag: Any, key: String)
    {
        REMOVE_METHOD.invoke(tag, key)
    }

    @JvmStatic
    fun getCompound(tag: Any, key: String): Any
    {
        return GET_COMPOUND_METHOD.invoke(tag, key)
    }

    @JvmStatic
    fun setString(tag: Any, key: String, value: String)
    {
        SET_STRING_METHOD.invoke(tag, key, value)
    }

    @JvmStatic
    fun getString(tag: Any, key: String): String
    {
        return GET_STRING_METHOD.invoke(tag, key) as String
    }

    @JvmStatic
    fun convertString(nbtString: Any): String
    {
        if (MinecraftReflection.isModern())
        {
            return NBT_TAG_STRING_VALUE_MODERN!!.get(nbtString) as String
        }

        return NBT_TAG_STRING_READ_METHOD!!.invoke(nbtString) as String
    }

//    @JvmStatic
//    fun setUUID(tag: Any, key: String, uuid: UUID) {
//        SET_UUID_METHOD.invoke(tag, key, uuid)
//    }

//    @JvmStatic
//    fun getUUID(tag: Any, key: String): UUID {
//        return GET_UUID_METHOD.invoke(tag, key) as UUID
//    }

    @JvmStatic
    fun getInt(tag: Any, key: String): Int
    {
        return GET_INT_METHOD.invoke(tag, key) as Int
    }

    @JvmStatic
    fun setInt(tag: Any, key: String, value: Int)
    {
        SET_INT_METHOD.invoke(tag, key, value)
    }

    @JvmStatic
    fun getDouble(tag: Any, key: String): Double
    {
        return GET_DOUBLE_METHOD.invoke(tag, key) as Double
    }

    @JvmStatic
    fun setDouble(tag: Any, key: String, value: Double)
    {
        SET_DOUBLE_METHOD.invoke(tag, key, value)
    }

    @JvmStatic
    fun getLong(tag: Any, key: String): Long
    {
        return GET_LONG_METHOD.invoke(tag, key) as Long
    }

    @JvmStatic
    fun setLong(tag: Any, key: String, value: Long)
    {
        SET_LONG_METHOD.invoke(tag, key, value)
    }

    @JvmStatic
    fun getFloat(tag: Any, key: String): Float
    {
        return GET_FLOAT_METHOD.invoke(tag, key) as Float
    }

    @JvmStatic
    fun setFloat(tag: Any, key: String, value: Float)
    {
        SET_FLOAT_METHOD.invoke(tag, key, value)
    }

    @JvmStatic
    fun isNumber(base: Any): Boolean
    {
        return NBT_NUMBER_CLASS.isAssignableFrom(base::class.java)
    }

    @JvmStatic
    fun isInt(base: Any): Boolean
    {
        return base::class.java == NBT_INT_CLASS
    }

    @JvmStatic
    fun isLong(base: Any): Boolean
    {
        return base::class.java == NBT_LONG_CLASS
    }

    @JvmStatic
    fun isDouble(base: Any): Boolean
    {
        return base::class.java == NBT_DOUBLE_CLASS
    }

    @JvmStatic
    fun isFloat(base: Any): Boolean
    {
        return base::class.java == NBT_FLOAT_CLASS
    }

    @JvmStatic
    fun getNumber(itemTag: Any, key: String): Number
    {
        val value = get(itemTag, key)
        return when
        {
            isInt(value) ->
            {
                getInt(itemTag, key)
            }

            isDouble(value) ->
            {
                getDouble(itemTag, key)
            }

            isLong(value) ->
            {
                getLong(itemTag, key)
            }

            isFloat(value) ->
            {
                getFloat(itemTag, key)
            }

            else -> 0
        }
    }

}

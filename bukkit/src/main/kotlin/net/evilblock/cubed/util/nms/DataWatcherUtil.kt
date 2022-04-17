/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.nms

import net.evilblock.cubed.util.Reflection
import org.apache.commons.lang3.ObjectUtils
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.experimental.and

object DataWatcherUtil
{
    private val ENTITY_CLASS =
        MinecraftReflection.getNMSClass("Entity")
            ?: Reflection.getClassSuppressed(
                "net.minecraft.world.Entity"
            )!!

    private val DATA_WATCHER_CLASS =
        MinecraftReflection.getNMSClass("DataWatcher")
            ?: Reflection.getClassSuppressed(
                "net.minecraft.network.syncher.DataWatcher"
            )!!

    private val DATA_WATCHER_CONSTRUCTOR =
        Reflection.getConstructor(DATA_WATCHER_CLASS, ENTITY_CLASS)!!

    private val DATA_WATCHER_REGISTER_METHOD: Method =
        Reflection.getDeclaredMethod(DATA_WATCHER_CLASS, "a", Int::class.java, Any::class.java)!!

    private val DATA_WATCHER_GET_WATCHED_OBJECT: Method =
        Reflection.getDeclaredMethod(DATA_WATCHER_CLASS, "j", Int::class.java)!!

    private var DATA_WATCHER_GET_WATCHED_OBJECTS_METHOD: Method =
        Reflection.getDeclaredMethod(DATA_WATCHER_CLASS, "c")!!

    private var DATA_WATCHER_DIRTY_FIELD: Field = Reflection.getDeclaredField(DATA_WATCHER_CLASS, "e")!!

    private val DATA_WATCHER_OBJECT_CLASS: Class<*> = MinecraftReflection.getNMSClass("DataWatcher\$WatchableObject")!!

    private val DATA_WATCHER_OBJECT_SET_VALUE: Method =
        Reflection.getDeclaredMethod(DATA_WATCHER_OBJECT_CLASS, "a", Any::class.java)!!

    private val DATA_WATCHER_OBJECT_GET_VALUE: Method = Reflection.getDeclaredMethod(DATA_WATCHER_OBJECT_CLASS, "b")!!

    private val DATA_WATCHER_OBJECT_MARK_DIRTY: Method =
        Reflection.getDeclaredMethod(DATA_WATCHER_OBJECT_CLASS, "a", Boolean::class.java)!!

    @JvmStatic
    fun new(): Any
    {
        return DATA_WATCHER_CONSTRUCTOR.newInstance(null)
    }

    @JvmStatic
    fun getWatchedObjects(dataWatcher: Any): List<Any>
    {
        return DATA_WATCHER_GET_WATCHED_OBJECTS_METHOD.invoke(dataWatcher)!! as List<Any>
    }

    @JvmStatic
    fun get(dataWatcher: Any, key: Int): Any
    {
        return DATA_WATCHER_GET_WATCHED_OBJECT.invoke(dataWatcher, key)
    }

    @JvmStatic
    fun <T> set(dataWatcher: Any, key: Int, value: T)
    {
        val dataWatcherItem = get(dataWatcher, key)
        val dataWatcherItemValue = getWatchedObjectValue(dataWatcherItem)

        if (ObjectUtils.notEqual(value, dataWatcherItemValue))
        {
            setWatchedObjectValue(dataWatcherItem, value as Any)
            markWatchedObjectDirty(dataWatcherItem, true)
            markDirty(dataWatcher)
        }
    }

    @JvmStatic
    fun register(dataWatcher: Any, key: Int, value: Any)
    {
        DATA_WATCHER_REGISTER_METHOD.invoke(dataWatcher, key, value)
    }

    @JvmStatic
    fun markDirty(dataWatcher: Any)
    {
        DATA_WATCHER_DIRTY_FIELD.set(dataWatcher, true)
    }

    @JvmStatic
    fun setFlag(dataWatcher: Any, i: Int, flag: Boolean)
    {
        val bitmask = getWatchedObjectValue(get(dataWatcher, 0)) as Byte
        if (flag)
        {
            set(dataWatcher, 0, (bitmask.toInt() or 1 shl i).toByte())
        } else
        {
            set(dataWatcher, 0, (bitmask.toInt() and 1 shl i.inv()).toByte())
        }
    }

    @JvmStatic
    fun getTypeFlag(dataWatcher: Any, key: Int, i: Int): Boolean
    {
        return (getWatchedObjectValue(get(dataWatcher, key)) as Byte) and i.toByte() != 0.toByte()
    }

    @JvmStatic
    fun setTypeFlag(dataWatcher: Any, key: Int, i: Int, flag: Boolean)
    {
        val bitmask = getWatchedObjectValue(get(dataWatcher, key)) as Byte
        if (flag)
        {
            set(dataWatcher, key, (bitmask.toInt() or i).toByte())
        } else
        {
            set(dataWatcher, key, (bitmask.toInt() and i.inv()).toByte())
        }
    }

    @JvmStatic
    fun setTypeFlagInt(dataWatcher: Any, key: Int, i: Int, flag: Boolean)
    {
        val mask = getWatchedObjectValue(get(dataWatcher, key)) as Int
        if (flag)
        {
            set(dataWatcher, key, mask or i)
        } else
        {
            set(dataWatcher, key, mask or i.inv())
        }
    }

    @JvmStatic
    fun getWatchedObjectValue(watchedObject: Any): Any
    {
        return DATA_WATCHER_OBJECT_GET_VALUE.invoke(watchedObject)
    }

    @JvmStatic
    fun setWatchedObjectValue(watchedObject: Any, value: Any)
    {
        DATA_WATCHER_OBJECT_SET_VALUE.invoke(watchedObject, value)
    }

    @JvmStatic
    fun markWatchedObjectDirty(watchedObject: Any, dirty: Boolean)
    {
        DATA_WATCHER_OBJECT_MARK_DIRTY.invoke(watchedObject, dirty)
    }

    @JvmStatic
    fun isWatchedObjectDirty(watchedObject: Any): Boolean
    {
        return Reflection.callMethod(watchedObject, "d")!! as Boolean
    }

}

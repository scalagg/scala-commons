/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package gg.scala.commons.event

import io.reactivex.rxjava3.core.Flowable
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class AbstractEvent<T : Any> : Event()
{
    companion object
    {
        @JvmStatic
        val handlerList = HandlerList()
    }

    override fun getHandlers() = handlerList

    abstract fun postPushCompute(): T
    abstract fun postPushFailure(throwable: Throwable): T

    private fun push(): T
    {
        kotlin
            .runCatching {
                Bukkit.getPluginManager().callEvent(this)
            }
            .onFailure {
                return this.postPushFailure(it)
            }

        return this.postPushCompute()
    }

    fun flow(): Flowable<T> = Flowable
        .fromCallable { push() }
}

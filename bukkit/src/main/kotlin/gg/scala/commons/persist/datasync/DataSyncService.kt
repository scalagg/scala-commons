package gg.scala.commons.persist.datasync

import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.ExtendedScalaPlugin
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers

/**
 * @author GrowlyX
 * @since 2/10/2023
 */
abstract class DataSyncService<T>
{
    private var cached: T? = null
    private val cache by lazy {
        sync().internal().connect()
    }

    abstract fun keys(): DataSyncKeys
    abstract fun type(): Class<T>

    fun cached() = cached
        ?: throw IllegalStateException(
            "DataSyncService did not load facets for ${type().name}!"
        )

    open fun cache() = cache!!
    open fun sync() = ScalaCommonsSpigot.instance.aware
    open fun plugin(): ExtendedScalaPlugin =
        ScalaCommonsSpigot.instance

    open fun cleanup()
    {

    }

    private var initialReloadComplete = false

    open fun postReload()
    {
        if (initialReloadComplete)
        {
            plugin().logger.info(
                "[datasync] reloaded ${
                    type().name
                } facets for ${
                    javaClass.name
                }"
            )
        }

        initialReloadComplete = true
    }

    fun load()
    {
        sync()
            .listen(
                keys().sync().asString()
            ) {
                reload()
            }

        reload()
        plugin().logger.info(
            "[datasync] loaded service ${
                javaClass.name
            }, listening on key ${
                keys().sync().asString()
            }"
        )
    }

    internal fun reload()
    {
        val model = cache().sync()
            .get(
                keys().store().asString()
            )

        if (model == null)
        {
            cached = type()
                .getDeclaredConstructor()
                .newInstance()

            cache().sync()
                .set(
                    keys().store().asString(),
                    Serializers.gson.toJson(cached)
                )

            pushKvUpdate()
            return
        }

        kotlin.runCatching {
            cleanup()
        }.onFailure {
            it.printStackTrace()
        }

        this.cached = Serializers.gson
            .fromJson(
                model, type()
            )
            ?: type()
                .getDeclaredConstructor()
                .newInstance()

        kotlin.runCatching {
            postReload()
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun sync(
        newModel: T
    )
    {
        cached = newModel

        cache().sync()
            .set(
                keys().store().asString(),
                Serializers.gson.toJson(cached)
            )

        pushKvUpdate()
    }

    private fun pushKvUpdate()
    {
        AwareMessage
            .of(
                keys().sync().asString(),
                sync()
            )
            .publish(
                AwareThreadContext.ASYNC
            )
    }
}

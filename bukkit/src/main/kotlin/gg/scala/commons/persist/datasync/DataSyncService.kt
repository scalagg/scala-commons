package gg.scala.commons.persist.datasync

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.UpdateOptions
import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.store.spigot.ScalaDataStoreSpigotImpl
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.serializers.Serializers
import org.bson.Document
import java.util.concurrent.CompletableFuture
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

abstract class DataSyncService<T>
{
    private var cached: T? = null
    private val lock = ReentrantReadWriteLock()
    private val cache = ScalaCommonsSpigot.instance.kvConnection

    private var collection: MongoCollection<Document>? = null
    private val reloadCallbacks = mutableListOf<() -> Unit>()

    open fun locatedIn(): DataSyncSource
    {
        return DataSyncSource.Redis
    }

    abstract fun keys(): DataSyncKeys
    abstract fun type(): Class<T>

    fun cached() = lock.read { cached }
        ?: throw IllegalStateException(
            "DataSyncService did not load facets for ${type().name}!"
        )

    open fun cache() = cache!!
    open fun sync() = ScalaCommonsSpigot.instance.aware
    open fun plugin(): ExtendedScalaPlugin =
        ScalaCommonsSpigot.instance

    fun onReload(callback: () -> Unit)
    {
        reloadCallbacks += callback
    }

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
                } in ${
                    locatedIn()
                }"
            )
        }

        initialReloadComplete = true

        reloadCallbacks.forEach { callback ->
            kotlin.runCatching { callback() }.onFailure { it.printStackTrace() }
        }
    }

    fun load()
    {
        if (locatedIn() == DataSyncSource.Mongo)
        {
            val resource = ScalaDataStoreSpigotImpl
                .getNewMongoConnection()
                .getAppliedResource()

            collection = resource.getCollection("DataSync")
        }

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
            } for changes to the copy in ${
                locatedIn()
            }"
        )
    }

    private fun pullModelFromSource(): String?
    {
        if (collection != null)
        {
            return collection!!
                .find(Filters.eq("uid", keys().newStore()))
                .first()?.toJson()
        }

        return cache().sync()
            .get(
                keys().newStore()
            )
    }

    private fun pushNewModelToSource(model: T)
    {
        if (collection != null)
        {
            val tree = Serializers.gson.toJsonTree(model)
            tree.asJsonObject.addProperty(
                "uid",
                keys().newStore()
            )

            collection!!.updateOne(
                Filters.eq("uid", keys().newStore()),
                Document(
                    "\$set",
                    Document.parse(
                        Serializers.gson.toJson(tree)
                    )
                ),
                UpdateOptions().upsert(true)
            )
            return
        }

        cache().sync()
            .set(
                keys().newStore(),
                Serializers.gson.toJson(model)
            )
    }

    internal fun reload()
    {
        val model = pullModelFromSource()

        if (model == null)
        {
            lock.write {
                cached = type()
                    .getDeclaredConstructor()
                    .newInstance()
            }

            pushNewModelToSource(cached())
            pushKvUpdate()
            return
        }

        kotlin.runCatching {
            cleanup()
        }.onFailure {
            it.printStackTrace()
        }

        lock.write {
            cached = Serializers.gson
                .fromJson(
                    model, type()
                )
                ?: type()
                    .getDeclaredConstructor()
                    .newInstance()
        }

        kotlin.runCatching {
            postReload()
        }.onFailure {
            it.printStackTrace()
        }
    }

    fun sync(newModel: T): CompletableFuture<Void>
    {
        lock.write {
            cached = newModel
        }

        return CompletableFuture.runAsync {
            pushNewModelToSource(cached())
            pushKvUpdate()
        }
    }

    fun editAndSave(dsl: T.() -> Unit)
    {
        with(cached()) {
            dsl()
            sync(this)
        }
    }

    private fun pushKvUpdate()
    {
        AwareMessage
            .of(
                keys().sync().asString(),
                sync()
            )
            .publish(
                AwareThreadContext.SYNC
            )
    }
}

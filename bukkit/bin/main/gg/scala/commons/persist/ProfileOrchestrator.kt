package gg.scala.commons.persist

import gg.scala.aware.message.AwareMessage
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.store.controller.DataStoreObjectController
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.storable.IDataStoreObject
import gg.scala.store.storage.type.DataStoreStorageType
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * @author GrowlyX
 * @since 4/16/2022
 */
abstract class ProfileOrchestrator<T : IDataStoreObject>
{
    lateinit var controller: DataStoreObjectController<T>
    private var readOnly = false

    fun find(player: Player): T? =
        controller.localCache()[player.uniqueId]

    fun find(uniqueId: UUID): T? =
        controller.localCache()[uniqueId]

    /**
     * Enables read-only mode, skipping auto-save on logout.
     */
    fun enableReadOnlyProfile()
    {
        readOnly = true
    }

    fun subscribe()
    {
        try
        {
            // we don't want to reinitialize the controller
            controller
            return
        } catch (_: Exception)
        {

        }

        controller = DataStoreObjectControllerCache.create(type())
        ProfileOrchestratorService.registerSelf(this)
    }

    open fun preLogout(profile: T)
    {

    }

    open fun postLoad(uniqueId: UUID)
    {

    }

    fun logoutSequenceFor(uniqueId: UUID): CompletableFuture<Void>
    {
        val profile = controller.localCache()[uniqueId]
            ?: return CompletableFuture.completedFuture(null)

        return CompletableFuture
            .runAsync {
                preLogout(profile)
            }
            .thenComposeAsync {
                if (readOnly) CompletableFuture.completedFuture(null)
                else controller.save(profile, DataStoreStorageType.MONGO)
            }
            .thenRun {
                controller.localCache().remove(uniqueId)
            }
            .exceptionally {
                Logger.getAnonymousLogger()
                    .log(
                        Level.SEVERE,
                        "Failed to save copy of ${type().simpleName} for $uniqueId on logout",
                        it
                    )
                return@exceptionally null
            }
    }

    fun saveAndUpdateGlobally(profile: T) = save(profile)
        .thenRunAsync {
            AwareMessage
                .of(
                    "update",
                    ProfileOrchestratorService.aware,
                    "player" to profile.identifier,
                    "type" to profile.javaClass.name
                )
                .publish(AwareThreadContext.SYNC)
        }

    fun updateLocalCache(uniqueId: UUID) = loadCopyOf(uniqueId)
        .thenAccept {
            controller.localCache().computeIfPresent(uniqueId) { _, _ -> it }
        }

    fun <V> useAsyncThenSaveGlobally(
        uniqueId: UUID,
        block: (T) -> V
    ) = CompletableFuture
        .supplyAsync {
            find(uniqueId)
        }
        .thenComposeAsync {
            if (it == null)
            {
                return@thenComposeAsync loadCopyOf(uniqueId)
                    .thenApplyAsync { profile ->
                        profile to block(profile)
                    }
            }

            CompletableFuture.completedFuture(it to block(it))
        }
        .thenComposeAsync { value ->
            saveAndUpdateGlobally(value.first)
                .thenApplyAsync {
                    value.second
                }
        }

    fun loadCopyOf(uniqueId: UUID) = controller
        .loadOptimalCopy(
            uniqueId
        ) {
            new(uniqueId).apply {
                save(this)
            }
        }
        .exceptionally {
            Logger.getAnonymousLogger()
                .log(
                    Level.SEVERE,
                    "Failed to load copy of ${type().simpleName} for $uniqueId on login",
                    it
                )
            return@exceptionally null
        }

    fun save(profile: T) = controller.save(profile, DataStoreStorageType.MONGO)

    abstract fun type(): KClass<T>
    abstract fun new(uniqueId: UUID): T
}

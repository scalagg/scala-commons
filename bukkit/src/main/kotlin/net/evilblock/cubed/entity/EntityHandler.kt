/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.backup.BackupHandler
import net.evilblock.cubed.config.CommonsGeneralConfig
import net.evilblock.cubed.entity.thread.EntityTickThread
import net.evilblock.cubed.serializers.Serializers
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
object EntityHandler
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    private val dataType = object : TypeToken<List<Entity>>()
    {}.type

    private val dataFile by lazy {
        File(plugin.dataFolder, "entities.json")
    }

    internal var loaded: Boolean = false
    internal var saving: Boolean = false

    /**
     * Entities that are tracked by any player that the entity is visible to.
     */
    private val entities: MutableSet<Entity> = ConcurrentHashMap.newKeySet(1024)

    /**
     * Instance entities are entities that are tracked by one player rather than multiple.
     */
    private val instanceEntities: MutableMap<UUID, MutableSet<Entity>> = ConcurrentHashMap()

    /**
     * These maps purpose is to provide a way to perform quick entity lookups.
     *
     * This technique consumes more memory rather than more processing power, and
     * that's optimal for our setup.
     */
    private val entitiesById: MutableMap<Int, Entity> = ConcurrentHashMap(1024)
    private val entitiesByUuid: MutableMap<UUID, Entity> = ConcurrentHashMap(1024)

    /**
     * A set of players that have debug view enabled.
     */
    private val debugView: MutableSet<UUID> = ConcurrentHashMap.newKeySet(10)

    @Configure
    fun configure()
    {
        if (dataFile.exists())
        {
            if (plugin.config<CommonsGeneralConfig>().backups)
            {
                Files.copy(dataFile, BackupHandler.findNextBackupFile("entities"))
            }

            Files.newReader(dataFile, Charsets.UTF_8).use { reader ->
                val entities = Serializers.gson.fromJson(reader, object : TypeToken<List<Entity>>()
                {}.type) as List<Entity>
                mapExistingEntities(entities)
            }
        }

        EntityTickThread.start()

        loaded = true
    }

    @Close
    fun close()
    {
        if (!loaded)
        {
            return
        }

        if (saving)
        {
            return
        }

        saving = true

        try
        {
            val json = Serializers.gson.toJson(entities.filter { it.persistent }, dataType)
            java.nio.file.Files.write(dataFile.toPath(), json.toByteArray())
        } catch (e: Exception)
        {
            e.printStackTrace()
        } finally
        {
            saving = false
        }
    }

    private fun mapExistingEntities(entities: List<Entity>)
    {
        for (entity in entities)
        {
            trackEntity(entity)
        }
    }

    fun getEntities(): Set<Entity>
    {
        return entities
    }

    fun <T : Entity> getEntitiesByType(type: Class<out T>): List<T>
    {
        return entities.filterIsInstance(type).toList()
    }

    fun getEntityByUuid(uuid: UUID): Entity?
    {
        return this.entitiesByUuid[uuid]
    }

    fun getEntityById(id: Int): Entity?
    {
        return this.entitiesById[id]
    }

    fun isEntityTracked(entity: Entity): Boolean
    {
        return this.entities.contains(entity)
    }

    fun trackEntity(entity: Entity)
    {
        this.entities.add(entity)
        this.entitiesById[entity.id] = entity
        this.entitiesByUuid[entity.uuid] = entity
    }

    fun forgetEntity(entity: Entity)
    {
        entity.onDeletion()

        this.entities.remove(entity)
        this.entitiesById.remove(entity.id)
        this.entitiesByUuid.remove(entity.uuid)
    }

    fun isTrackingInstanceEntities(player: Player): Boolean
    {
        return this.instanceEntities.containsKey(player.uniqueId)
    }

    fun startTrackingInstanceEntities(player: Player)
    {
        this.instanceEntities[player.uniqueId] = ConcurrentHashMap.newKeySet()
    }

    fun stopTrackingInstanceEntities(player: Player): MutableSet<Entity>
    {
        return this.instanceEntities.remove(player.uniqueId)!!
    }

    fun getInstanceEntities(player: Player): MutableSet<Entity>
    {
        return this.instanceEntities[player.uniqueId]!!
    }

    fun isInstanceEntityTracked(player: Player, entity: Entity): Boolean
    {
        return this.instanceEntities[player.uniqueId]!!.contains(entity)
    }

    fun trackInstanceEntity(player: Player, entity: Entity)
    {
        this.instanceEntities[player.uniqueId]!!.add(entity)
    }

    fun forgetInstanceEntity(player: Player, entity: Entity): MutableSet<Entity>
    {
        return this.instanceEntities.remove(player.uniqueId)!!
    }

    fun isDebugViewEnabled(player: Player): Boolean
    {
        return debugView.contains(player.uniqueId)
    }

    fun toggleDebugView(player: Player): Boolean
    {
        return if (debugView.contains(player.uniqueId))
        {
            debugView.remove(player.uniqueId)
            false
        } else
        {
            debugView.add(player.uniqueId)
            true
        }
    }

}

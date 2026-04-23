/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity

import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.entity.animation.EntityAnimationType
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.config.CommonsGeneralConfig
import net.evilblock.cubed.entity.hooks.EntityHooks
import net.evilblock.cubed.entity.menu.button.*
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

abstract class Entity(var location: Location)
{

    /**
     * The ID of this entity.
     */
    @Transient
    var id: Int = -1

    /**
     * The unique ID of this entity. Persistent across server restarts.
     */
    val uuid: UUID = UUID.randomUUID()

    @Transient
    private var initialized: Boolean = false

    /**
     * If this entity is hidden or not.
     */
    @Transient
    private var hidden: Boolean = false

    /**
     * If this entity is persistent across server restarts.
     */
    @Transient
    var persistent: Boolean = true

    /**
     * If this entity is the root entity of a multi-part entity.
     */
    @Transient
    var root: Boolean = true

    /**
     * This entity's animation.
     */
    var animation: EntityAnimation? = null

    /**
     * The players currently viewing this entity.
     */
    @Transient
    protected lateinit var currentWatchers: MutableSet<UUID>

    @Transient
    internal lateinit var debugView: MutableMap<UUID, HologramEntity>

    var command: String? = null
    var condition: String? = null

    var motionX: Double = 0.0
    var motionY: Double = 0.0
    var motionZ: Double = 0.0

    abstract fun getTypeName(): String

    /**
     * Each transient field (fields that are not serialized) of an [Entity] implementation
     * must be initialized in this method as GSON deserialization sets transient field values
     * to null.
     *
     * Therefore, this method is called immediately after deserialization.
     */
    open fun initializeData()
    {
        this.id = nextEntityId()
        this.persistent = true
        this.root = true
        this.currentWatchers = ConcurrentHashMap.newKeySet()
        this.debugView = ConcurrentHashMap()

        if (isMultiPartEntity())
        {
            for (childEntity in getChildEntities())
            {
                childEntity.initializeData()
                childEntity.persistent = false
                childEntity.root = false
            }
        }

        initialized = true

        if (animation != null)
        {
            animation!!.entity = this
        }
    }

    open fun onDeletion()
    {
        destroyForCurrentWatchers()

        if (isMultiPartEntity())
        {
            for (child in getChildEntities())
            {
                child.destroyForCurrentWatchers()
                child.onDeletion()
            }
        }
    }

    /**
     * Updates the [Entity.location] of this entity to the given [location] and sends updates to the current watchers.
     */
    open fun updateLocation(location: Location)
    {
        this.location = location

        for (player in getCurrentWatcherPlayers())
        {
            if (isVisibleToPlayer(player))
            {
                sendRepositionPackets(player)
            }
        }

        if (debugView.isNotEmpty())
        {
            for (hologram in debugView.values)
            {
                hologram.updateLocation(getDebugViewLocation())
            }
        }
    }

    /**
     * Gets a prepped location that's used to spawn/reposition an entity.
     */
    open fun getAdjustedLocation(player: Player): Location
    {
        return location
    }

    /**
     *
     */
    open fun getDebugViewLocation(): Location
    {
        return location.clone().subtract(0.0, 0.0, 0.0)
    }

    open fun getAttachedHologram(): HologramEntity?
    {
        return null
    }

    fun isInitialized(): Boolean
    {
        return initialized
    }

    /**
     * If this entity is visible.
     */
    open fun isVisible(): Boolean
    {
        return !hidden
    }

    /**
     * If this entity is visible to the given [player].
     */
    open fun isVisibleToPlayer(player: Player): Boolean
    {
        return !hidden && location.world == player.world && location.distance(player.location) <= EntityHooks.entityVisibilityDistance(player)
    }

    /**
     * Updates the visibility of this entity.
     */
    open fun updateVisibility(hidden: Boolean)
    {
        val hasChanged = hidden != this.hidden

        this.hidden = hidden

        if (hasChanged)
        {
            if (this.hidden)
            {
                for (player in getCurrentWatcherPlayers())
                {
                    destroy(player)
                }
            } else
            {
                for (player in getCurrentWatcherPlayers())
                {
                    spawn(player)
                }
            }
        }
    }

    /**
     * Called when the given [player] left-clicks this entity.
     */
    open fun onLeftClick(player: Player)
    {
    }

    /**
     * Called when the given [player] right-clicks this entity.
     */
    open fun onRightClick(player: Player)
    {
        if (command != null)
        {
            player.chat("/$command")
        }
    }

    open fun isDamageable(): Boolean
    {
        return false
    }

    open fun spawn(player: Player)
    {
        if (
            !ScalaCommonsSpigot.instance.config<CommonsGeneralConfig>().enable1Dot7Entities &&
            MinecraftProtocol.getPlayerVersion(player) == 5
        )
        {
            return
        }

        if (!::currentWatchers.isInitialized)
        {
            return
        }

        if (currentWatchers.contains(player.uniqueId))
        {
            return
        }

        currentWatchers.add(player.uniqueId)

        sendSpawnPackets(player)
        sendUpdatePackets(player)

        val multiPart = isMultiPartEntity()
        if (multiPart)
        {
            for (childEntity in getChildEntities())
            {
                childEntity.spawn(player)
            }
        }
    }

    open fun update(player: Player)
    {
        if (!::currentWatchers.isInitialized)
        {
            return
        }

        if (!currentWatchers.contains(player.uniqueId))
        {
            return
        }

        sendUpdatePackets(player)

        if (isMultiPartEntity())
        {
            for (childEntity in getChildEntities())
            {
                childEntity.update(player)
            }
        }
    }

    open fun destroy(player: Player)
    {
        if (::currentWatchers.isInitialized)
        {
            currentWatchers.remove(player.uniqueId)
        }

        sendDestroyPackets(player)

        if (isMultiPartEntity())
        {
            for (childEntity in getChildEntities())
            {
                childEntity.destroy(player)
            }
        }

        debugView.remove(player.uniqueId)?.destroyForCurrentWatchers()
    }

    fun hasAnyWatchers(): Boolean
    {
        return ::currentWatchers.isInitialized && currentWatchers.isNotEmpty()
    }

    /**
     * Gets a list of [Player] objects mapped from the [currentWatchers] list.
     */
    fun getCurrentWatcherPlayers(): List<Player>
    {
        if (!::currentWatchers.isInitialized)
        {
            return emptyList()
        }

        return currentWatchers.mapNotNull { Bukkit.getPlayer(it) }
    }

    fun isWatcher(playerUuid: UUID): Boolean
    {
        if (!::currentWatchers.isInitialized)
        {
            return false
        }

        return currentWatchers.contains(playerUuid)
    }

    /**
     * Sends entity updates to all current watchers.
     */
    open fun updateForCurrentWatchers()
    {
        for (player in getCurrentWatcherPlayers())
        {
            update(player)
        }
    }

    /**
     * Destroys the entity for all current watchers.
     */
    open fun destroyForCurrentWatchers()
    {
        for (player in getCurrentWatcherPlayers())
        {
            destroy(player)
        }
    }

    open fun resendForCurrentWatchers()
    {
        for (player in getCurrentWatcherPlayers())
        {
            destroy(player)
            spawn(player)
        }
    }

    open fun isMultiPartEntity(): Boolean
    {
        return false
    }

    open fun isRootOfMultiPartEntity(): Boolean
    {
        return root
    }

    fun canDisplayDebug(): Boolean
    {
        val multiPart = isMultiPartEntity()
        return !multiPart || isRootOfMultiPartEntity()
    }

    open fun renderDebugInformation(): List<String>
    {
        return listOf("(ID: #${id})")
    }

    open fun getChildEntities(): Set<Entity>
    {
        return emptySet()
    }

    open fun getEditorButtons(player: Player, menu: Menu): MutableList<Button>
    {
        return arrayListOf<Button>().also { buttons ->
            buttons.add(UpdateCommandButton(this))
            buttons.add(MoveLocationButton(this))
            buttons.add(EditConditionButton(this))

            if (getAnimationTypes().isNotEmpty())
            {
                buttons.add(EditAnimationButton(this))
            }

            buttons.add(DeleteButton(this))
        }
    }

    open fun getAnimationTypes(): List<EntityAnimationType>
    {
        return emptyList()
    }

    open fun sendSpawnPackets(player: Player)
    {
    }

    open fun sendDestroyPackets(player: Player)
    {
        if (MinecraftReflection.isModern())
        {
            MinecraftProtocol.send(player, MinecraftProtocol.buildModernRemoveEntitiesPacket(id))
            return
        }

        val destroyPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityDestroy")
        Reflection.setDeclaredFieldValue(destroyPacket, "a", intArrayOf(id))

        MinecraftProtocol.send(player, destroyPacket)
    }

    open fun sendUpdatePackets(player: Player)
    {
    }

    open fun sendRepositionPackets(player: Player)
    {
        MinecraftProtocol.send(player, MinecraftProtocol.buildEntityTeleportPacket(id, location))
    }

    fun sendStatusPacket(status: Byte)
    {
        val packet = MinecraftProtocol.newPacket("PacketPlayOutEntityStatus").also {
            Reflection.setDeclaredFieldValue(it, "a", id)
            Reflection.setDeclaredFieldValue(it, "b", status)
        }

        for (watcher in getCurrentWatcherPlayers())
        {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun sendAnimationPacket(id: Int)
    {
        val packet = MinecraftProtocol.newPacket("PacketPlayOutAnimation").also {
            Reflection.setDeclaredFieldValue(it, "a", this.id)
            Reflection.setDeclaredFieldValue(it, "b", id)
        }

        for (watcher in getCurrentWatcherPlayers())
        {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    companion object
    {
        const val initialId = 900_000_000
        private var entityId = initialId

        fun nextEntityId() = entityId++
        fun currId() = entityId
    }
}

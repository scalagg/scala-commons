

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

    @Transient
    var id: Int = -1

    val uuid: UUID = UUID.randomUUID()

    @Transient
    private var initialized: Boolean = false

    @Transient
    private var hidden: Boolean = false

    @Transient
    var persistent: Boolean = true

    @Transient
    var root: Boolean = true

    var animation: EntityAnimation? = null

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

    open fun getAdjustedLocation(player: Player): Location
    {
        return location
    }

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

    open fun isVisible(): Boolean
    {
        return !hidden
    }

    open fun isVisibleToPlayer(player: Player): Boolean
    {
        return !hidden && location.world == player.world && location.distance(player.location) <= EntityHooks.entityVisibilityDistance(player)
    }

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

    open fun onLeftClick(player: Player)
    {
    }

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

    open fun updateForCurrentWatchers()
    {
        for (player in getCurrentWatcherPlayers())
        {
            update(player)
        }
    }

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

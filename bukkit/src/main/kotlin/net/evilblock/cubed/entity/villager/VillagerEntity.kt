package net.evilblock.cubed.entity.villager

import com.comphenix.packetwrapper.WrapperPlayServerSpawnEntityLiving
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.living.LivingEntity
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.AngleUtils
import net.evilblock.cubed.util.nms.DataWatcherUtil
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

open class VillagerEntity(private var lines: List<String>, location: Location) : LivingEntity(location) {

    @Transient lateinit var hologram: HologramEntity

    override fun getTypeName(): String {
        return "Villager"
    }

    override fun initializeData() {
        createHologram()

        hologram.offset = true

        EntityHandler.trackEntity(hologram)

        super.initializeData()
    }

    override fun onDeletion() {
        EntityHandler.forgetEntity(hologram)

        super.onDeletion()
    }

    override fun updateLocation(location: Location) {
        super.updateLocation(location)

        hologram.updateLocation(calculateHologramLocation())
    }

    override fun getDebugViewLocation(): Location {
        return calculateHologramLocation().clone().subtract(0.0, 0.23, 0.0)
    }

    override fun getAttachedHologram(): HologramEntity {
        return hologram
    }

    override fun isMultiPartEntity(): Boolean {
        return true
    }

    override fun getChildEntities(): Set<Entity> {
        return setOf(hologram)
    }

    fun updateLines(newLines: List<String>) {
        lines = newLines.toMutableList()
        hologram.updateLines(newLines)
    }

    fun calculateHologramLocation(): Location {
        return location.clone().add(0.0, 0.75, 0.0)

//        return if (lines.size > 1) {
//            location.clone().add(0.0, 2.21, 0.0)
//        } else {
//            location.clone()
//        }
    }

    open fun createHologram() {
        hologram = HologramEntity(text = "", location = calculateHologramLocation(), parent = this)
    }

    override fun sendSpawnPackets(player: Player) {
        if (!MinecraftReflection.isModern())
        {
            val dataWatcher = DataWatcherUtil.new()
            DataWatcherUtil.register(dataWatcher, 0, 0.toByte()) // flag
            DataWatcherUtil.register(dataWatcher, 1, 300.toShort()) // air ticks
            DataWatcherUtil.register(dataWatcher, 2, "") // custom name
            DataWatcherUtil.register(dataWatcher, 3, 0.toByte()) // custom name visible
            DataWatcherUtil.register(dataWatcher, 4, 1.toByte()) // silent
            DataWatcherUtil.register(dataWatcher, 6, 1.toFloat()) // health
            DataWatcherUtil.register(dataWatcher, 10, 2.toByte()) // gravity
            DataWatcherUtil.register(dataWatcher, 12, 0.toByte()) // age
            DataWatcherUtil.register(dataWatcher, 16, 0) // profession

            val spawnEntityPacket = MinecraftProtocol.newPacket("PacketPlayOutSpawnEntityLiving")
            Reflection.setDeclaredFieldValue(spawnEntityPacket, "b", 120)
            Reflection.setDeclaredFieldValue(spawnEntityPacket, "a", id)
            Reflection.setDeclaredFieldValue(spawnEntityPacket, "j", 0.toByte())
            Reflection.setDeclaredFieldValue(spawnEntityPacket, "c", MinecraftReflection.mathFloor(location.x * 32.0))
            Reflection.setDeclaredFieldValue(spawnEntityPacket, "d", MinecraftReflection.mathFloor(location.y * 32.0))
            Reflection.setDeclaredFieldValue(spawnEntityPacket, "e", MinecraftReflection.mathFloor(location.z * 32.0))
            Reflection.setDeclaredFieldValue(spawnEntityPacket, "i", AngleUtils.yawToBytes(location.yaw))

            MinecraftProtocol.send(player, spawnEntityPacket)
        } else
        {
            val watcher = WrappedDataWatcher()
            watcher.setObject(0, 0.toByte()) // flag
            watcher.setObject(1, 300.toShort()) // air ticks
            watcher.setObject(2, "") // custom name
            watcher.setObject(3, 0.toByte()) // custom name visible
            watcher.setObject(4, 1.toByte()) // silent
            watcher.setObject(6, 1.toFloat()) // health
            watcher.setObject(10, 2.toByte()) // gravity
            watcher.setObject(12, 0.toByte()) // age
            watcher.setObject(16, 0) // profession

            val living = WrapperPlayServerSpawnEntityLiving()
            living.entityID = id
            living.metadata = watcher
            living.x = MinecraftReflection.mathFloor(location.x * 32.0).toDouble()
            living.y = MinecraftReflection.mathFloor(location.y * 32.0).toDouble()
            living.z = MinecraftReflection.mathFloor(location.z * 32.0).toDouble()
            living.yaw = AngleUtils.yawToBytes(location.yaw).toFloat()
            living.type = EntityType.VILLAGER
            living.sendPacket(player)
        }

        val entityLookPacket = MinecraftProtocol.buildEntityLookPacket(id, location.yaw, location.pitch)

        val entityHeadRotationPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityHeadRotation")
        Reflection.setDeclaredFieldValue(entityHeadRotationPacket, "a", id)
        Reflection.setDeclaredFieldValue(entityHeadRotationPacket, "b", AngleUtils.yawToBytes(location.yaw))

        val entityTeleportPacket = MinecraftProtocol.newPacket("PacketPlayOutEntityTeleport")
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "a", id)
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "b", MinecraftReflection.mathFloor(location.x * 32.0))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "c", MinecraftReflection.mathFloor(location.y * 32.0))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "d", MinecraftReflection.mathFloor(location.z * 32.0))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "e", AngleUtils.yawToBytes(location.yaw))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "f", AngleUtils.yawToBytes(location.pitch))
        Reflection.setDeclaredFieldValue(entityTeleportPacket, "g", true)

        MinecraftProtocol.send(player, entityLookPacket, entityHeadRotationPacket, entityTeleportPacket)
    }

}

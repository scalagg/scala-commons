/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.entity.npc

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.animation.EntityAnimationType
import net.evilblock.cubed.entity.hologram.HologramEntity
import net.evilblock.cubed.entity.hologram.menu.button.UpdateFloatingItemButton
import net.evilblock.cubed.entity.npc.animation.FloatAnimation
import net.evilblock.cubed.entity.npc.animation.MultiRingsAnimation
import net.evilblock.cubed.entity.npc.animation.RingsAnimation
import net.evilblock.cubed.entity.npc.menu.button.*
import net.evilblock.cubed.entity.npc.protocol.NpcProtocol
import net.evilblock.cubed.nametag.update.NametagUpdates
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.entity.npc.texture.NpcTexture
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ThreadLocalRandom

open class NpcEntity(private var lines: List<String>, location: Location) : Entity(location) {

    @Transient internal lateinit var nametag: NametagUpdates
    @Transient lateinit var hologram: HologramEntity
    @Transient internal lateinit var profile: Any
    @Transient internal lateinit var metadata: Any

    internal var itemInHand: ItemStack? = null
    internal var helmet: ItemStack? = null
    internal var chestplate: ItemStack? = null
    internal var leggings: ItemStack? = null
    internal var boots: ItemStack? = null

    private var textureValue: String? = null
    private var textureSignature: String? = null

    var nextPhrase: Long = randomTime()
    var phrases: List<List<String>> = arrayListOf()

    var messages: MutableList<String>? = null

    override fun getTypeName(): String {
        return "NPC"
    }

    open fun createHologram() {
        hologram = HologramEntity(
            text = "", location = calculateHologramLocation(), parent = this
        )
        hologram.offset = true
    }

    override fun initializeData() {
        createHologram()

        super.initializeData()

        nametag = NametagUpdates(text = "", invisible = true, priority = 99)
        profile = MinecraftReflection.newGameProfile(uuid, nametag.name)
        metadata = NpcProtocol.createDataWatcher(this)

        if (this.textureValue != null && this.textureSignature != null) {
            updateTexture(textureValue!!, textureSignature!!)
        }

        updateLines(lines)
    }

    override fun getDebugViewLocation(): Location {
        return hologram.getDebugViewLocation()
    }

    override fun getAttachedHologram(): HologramEntity {
        return hologram
    }

    override fun getAnimationTypes(): List<EntityAnimationType> {
        return listOf(
            RingsAnimation.RingsAnimationType,
            FloatAnimation.FloatAnimationType,
            MultiRingsAnimation.MultiRingsAnimationType
        )
    }

    override fun onRightClick(player: Player) {
        if (messages != null) {
            for (message in messages!!) {
                player.sendMessage(message)
            }
        }

        if (command != null) {
            player.chat("/$command")
        }
    }

    override fun destroy(player: Player) {
        super.destroy(player)

        nametag.clear(player, "NPC_$id")
    }

    override fun sendSpawnPackets(player: Player) {

        NpcProtocol.sendSpawnPackets(player, this)
    }

    override fun sendUpdatePackets(player: Player) {
        NpcProtocol.sendUpdatePackets(player, this)
    }

    override fun updateLocation(location: Location) {
        super.updateLocation(location)

        hologram.updateLocation(calculateHologramLocation())
    }

    override fun isMultiPartEntity(): Boolean {
        return true
    }

    override fun getChildEntities(): Set<Entity> {
        return setOf(hologram)
    }

    override fun getEditorButtons(player: Player, menu: Menu): MutableList<Button> {
        return super.getEditorButtons(player, menu).also {
            it.add(UpdateMessagesButton(this))
            it.add(UpdateSkinButton(this) { menu.openMenu(player) })
            it.add(UpdateTextButton(this))
            it.add(UpdateGearButton(this))
            it.add(PositionEditorButton(this))
            it.add(UpdateFloatingItemButton(hologram))
        }
    }

    fun getLines(): List<String> {
        return lines.toList()
    }

    fun calculateHologramLocation(): Location {
        val helmetOffset = if (helmet != null && helmet!!.type == Material.SKULL_ITEM) {
            0.1
        } else {
            0.0
        }

        return location.clone().add(0.0, 0.75 + helmetOffset, 0.0)

//        return if (lines.size > 1) {
//            location.clone().add(0.0, 2.06 + helmetOffset, 0.0)
//        } else {
//            location.clone().add(0.0, helmetOffset, 0.0)
//        }
    }

    fun updateLines(lines: List<String>) {
        this.lines = lines.toMutableList()
        hologram.updateLines(lines)
    }

    fun hasTexture(): Boolean {
        return textureValue != null && textureSignature != null
    }

    fun updateTexture(value: String?, signature: String?) {
        this.textureValue = value
        this.textureSignature = signature

        profile = MinecraftReflection.newGameProfile(
            MinecraftReflection.getGameProfileUUID(profile),
            MinecraftReflection.getGameProfileName(profile)
        )

        if (textureValue != null && textureSignature != null) {
            MinecraftReflection.setGameProfileTexture(profile, textureValue!!, textureSignature!!)
        }

        for (player in getCurrentWatcherPlayers()) {
            if (isVisibleToPlayer(player)) {
                destroy(player)
                spawn(player)
            }
        }
    }

    fun updateTextureByUsername(username: String, onComplete: (Boolean, Exception?) -> Unit) {
        try {
            val texture = NpcTexture
                .fetchTextureByUsername(username)
                .orElseThrow {
                    IllegalStateException("Failed to fetch texture by username")
                }

            updateTexture(texture!!.textureValue, texture.textureSignature)
            onComplete.invoke(true, null)
        } catch (e: Exception) {
            e.printStackTrace()
            onComplete(false, e)
        }
    }

    fun updateItemInHand(itemStack: ItemStack) {
        itemInHand = itemStack.clone()

        val packet = MinecraftProtocol.buildEntityEquipmentPacket(id, 0, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateHelmet(itemStack: ItemStack) {
        helmet = itemStack.clone()

        val packet = MinecraftProtocol.buildEntityEquipmentPacket(id, 4, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateChestplate(itemStack: ItemStack) {
        chestplate = itemStack.clone()

        val packet = MinecraftProtocol.buildEntityEquipmentPacket(id, 3, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateLeggings(itemStack: ItemStack) {
        leggings = itemStack.clone()

        val packet = MinecraftProtocol.buildEntityEquipmentPacket(id, 2, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updateBoots(itemStack: ItemStack) {
        boots = itemStack.clone()

        val packet = MinecraftProtocol.buildEntityEquipmentPacket(id, 1, itemStack)

        for (watcher in getCurrentWatcherPlayers()) {
            MinecraftProtocol.send(watcher, packet)
        }
    }

    fun updatePhrases(list: List<List<String>>) {
        phrases = list
    }

    fun getRandomPhrase(): List<String> {
        if (phrases.size == 1) {
            return phrases.first()
        }
        return phrases.random()
    }

    fun randomTime(origin: Long = 10_000L, bound: Long = 17_000L): Long {
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextLong(origin, bound)
    }

}

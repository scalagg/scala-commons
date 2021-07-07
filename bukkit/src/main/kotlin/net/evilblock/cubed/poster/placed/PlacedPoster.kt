/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.poster.placed

import com.google.gson.annotations.JsonAdapter
import net.evilblock.cubed.poster.Poster
import net.evilblock.cubed.poster.PosterRenderer
import net.evilblock.cubed.poster.placed.serialize.PosterItemFrameSerializer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapView

class PlacedPoster(
    @JsonAdapter(Poster.ReferenceSerializer::class)
    var poster: Poster,
    var persistent: Boolean = true
) {

    @JsonAdapter(PosterItemFrameSerializer::class)
    private val itemFrames: MutableList<ItemFrame> = arrayListOf()

    @Transient
    private var mapViews: MutableList<MapView> = arrayListOf()

    fun initializeData() {
        mapViews = arrayListOf()

        for (itemFrame in itemFrames) {
            setupItemFrame(itemFrame)
        }
    }

    fun clear(player: Player) {
        for (mapView in mapViews) {
            (mapView.renderers.first() as PosterRenderer).renderedFor.remove(player.uniqueId)
        }
    }

    fun getItemFrames(): List<ItemFrame> {
        return itemFrames
    }

    fun trackItemFrame(itemFrame: ItemFrame) {
        itemFrames.add(itemFrame)
    }

    fun forgetItemFrame(itemFrame: ItemFrame) {
        itemFrames.remove(itemFrame)
    }

    fun setupItemFrame(itemFrame: ItemFrame) {
        val mapView = Bukkit.getServer().createMap(itemFrames.first().world).also { mapView ->
            for (renderer in mapView.renderers) {
                mapView.removeRenderer(renderer)
            }

            mapView.addRenderer(PosterRenderer(poster))
            mapViews.add(mapView)
        }

        val mapItem = ItemStack(Material.MAP, 1, mapView.id)
        itemFrame.item = mapItem
    }

}
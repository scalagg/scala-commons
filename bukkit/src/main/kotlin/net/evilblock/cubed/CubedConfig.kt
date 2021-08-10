/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed

import net.evilblock.cubed.entity.npc.texture.NpcTexture

object CubedConfig {

    fun hasNpcTexture(id: String): Boolean {
        return Cubed.instance.config.contains("npc-textures.$id")
    }

    fun getNpcTexture(id: String): NpcTexture {
        return NpcTexture(
            textureValue = Cubed.instance.config.getString("npc-textures.$id.texture-value"),
            textureSignature = Cubed.instance.config.getString("npc-textures.$id.texture-signature")
        )
    }

    fun getNpcTextureIds(): Set<String> {
        return Cubed.instance.config.getConfigurationSection("npc-textures").getKeys(false)
    }

}

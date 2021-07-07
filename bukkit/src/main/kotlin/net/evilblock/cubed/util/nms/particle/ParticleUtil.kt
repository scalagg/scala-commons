package net.evilblock.cubed.util.nms.particle

import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.MinecraftProtocol
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

/**
 * A class to simplify sending particles to players.
 *
 * Also created to fix the issue of bukkit sometimes
 * not sending the particle or sending the particle
 * in varying speeds / sizes.
 */
object ParticleUtil {

    fun sendParticle(player: Player, vararg particleMetas: ParticleMeta) {
        val packets = arrayListOf<Pair<Location, Any>>()

        for (meta in particleMetas) {
            packets.add(meta.location to MinecraftProtocol.newPacket("PacketPlayOutWorldParticles").also { packet ->
                Reflection.setDeclaredFieldValue(packet, "a", meta.particle)
                Reflection.setDeclaredFieldValue(packet, "b", meta.location.x.toFloat())
                Reflection.setDeclaredFieldValue(packet, "c", meta.location.y.toFloat())
                Reflection.setDeclaredFieldValue(packet, "d", meta.location.z.toFloat())
                Reflection.setDeclaredFieldValue(packet, "e", meta.offsetX)
                Reflection.setDeclaredFieldValue(packet, "f", meta.offsetY)
                Reflection.setDeclaredFieldValue(packet, "g", meta.offsetZ)
                Reflection.setDeclaredFieldValue(packet, "h", meta.speed)
                Reflection.setDeclaredFieldValue(packet, "i", meta.amount)
                Reflection.setDeclaredFieldValue(packet, "j", true)
            })
        }

        for (packet in packets) {
            if (player.location.distance(packet.first) > 32.0) {
                continue
            }

            MinecraftProtocol.send(player, packet.second)
        }
    }

    fun sendsParticleToAll(world: World, vararg particleMetas: ParticleMeta) {
        val packets = arrayListOf<Pair<Location, Any>>()

        for (meta in particleMetas) {
            packets.add(meta.location to MinecraftProtocol.newPacket("PacketPlayOutWorldParticles").also { packet ->
                Reflection.setDeclaredFieldValue(packet, "a", meta.particle)
                Reflection.setDeclaredFieldValue(packet, "b", meta.location.x.toFloat())
                Reflection.setDeclaredFieldValue(packet, "c", meta.location.y.toFloat())
                Reflection.setDeclaredFieldValue(packet, "d", meta.location.z.toFloat())
                Reflection.setDeclaredFieldValue(packet, "e", meta.offsetX)
                Reflection.setDeclaredFieldValue(packet, "f", meta.offsetY)
                Reflection.setDeclaredFieldValue(packet, "g", meta.offsetZ)
                Reflection.setDeclaredFieldValue(packet, "h", meta.speed)
                Reflection.setDeclaredFieldValue(packet, "i", meta.amount)
                Reflection.setDeclaredFieldValue(packet, "j", true)
            })
        }

        for (player in world.players) {
            for (packet in packets) {
                if (player.location.distance(packet.first) > 32.0) {
                    continue
                }

                MinecraftProtocol.send(player, packet.second)
            }
        }
    }

}
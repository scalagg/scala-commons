package net.evilblock.cubed.util.nms.particle

import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.evilblock.cubed.util.nms.MinecraftReflection
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player

object ParticleUtil {

    private fun isBukkitParticle(particle: Any): Boolean {
        return particle.javaClass.name == "org.bukkit.Particle"
    }

    private fun sendModernParticle(player: Player, meta: ParticleMeta)
    {
        val method = player.javaClass.methods.firstOrNull {
            it.name == "spawnParticle" && it.parameterTypes.size == 8 && it.parameterTypes[0].name == "org.bukkit.Particle"
        } ?: return

        method.invoke(
            player,
            meta.particle,
            meta.location,
            if (meta.amount <= 0) 1 else meta.amount,
            meta.offsetX.toDouble(),
            meta.offsetY.toDouble(),
            meta.offsetZ.toDouble(),
            meta.speed.toDouble(),
            meta.data
        )
    }

    fun sendParticle(player: Player, vararg particleMetas: ParticleMeta) {
        if (MinecraftReflection.isModern() && particleMetas.all { isBukkitParticle(it.particle) }) {
            particleMetas.forEach {
                if (player.location.distance(it.location) <= 32.0) {
                    sendModernParticle(player, it)
                }
            }
            return
        }

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
        if (MinecraftReflection.isModern() && particleMetas.all { isBukkitParticle(it.particle) }) {
            for (player in world.players) {
                particleMetas.forEach {
                    if (player.location.distance(it.location) <= 32.0) {
                        sendModernParticle(player, it)
                    }
                }
            }
            return
        }

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

    fun sendsParticleToAllRespectingVisibility(world: World, sender: Player, vararg particleMetas: ParticleMeta) {
        if (MinecraftReflection.isModern() && particleMetas.all { isBukkitParticle(it.particle) }) {
            for (player in world.players) {
                particleMetas.forEach {
                    if (player.location.distance(it.location) <= 32.0 && player.canSee(sender)) {
                        sendModernParticle(player, it)
                    }
                }
            }
            return
        }

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

                if (player.canSee(sender))
                {
                    MinecraftProtocol.send(player, packet.second)
                }
            }
        }
    }

    fun sendsParticleToAllRespectingFilter(world: World, filter: (Player) -> Boolean, vararg particleMetas: ParticleMeta) {
        if (MinecraftReflection.isModern() && particleMetas.all { isBukkitParticle(it.particle) }) {
            for (player in world.players) {
                particleMetas.forEach {
                    if (player.location.distance(it.location) <= 32.0 && filter(player)) {
                        sendModernParticle(player, it)
                    }
                }
            }
            return
        }

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

                if (filter(player))
                {
                    MinecraftProtocol.send(player, packet.second)
                }
            }
        }
    }

}

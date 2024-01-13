package net.evilblock.cubed.entity.living

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.living.effect.EntityEffect
import net.evilblock.cubed.entity.living.event.EntityDamageEvent
import net.evilblock.cubed.entity.living.event.EntityDeathEvent
import net.evilblock.cubed.entity.tick.TickableEntity
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.util.nms.particle.ParticleMeta
import net.evilblock.cubed.util.nms.particle.ParticleUtil
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

abstract class LivingEntity(location: Location) : Entity(location = location), TickableEntity
{
    var health: Double = 20.0
    var dead: Boolean = false

    var attackable: Boolean = false
    var lastAttack: Long? = null

    var effects: MutableSet<EntityEffect> = ConcurrentHashMap.newKeySet()

    var width = 0.6F
    var length = 1.8F

    override fun tick()
    {
        tickEffects()
    }

    override fun spawn(player: Player)
    {
        if (dead)
        {
            return
        }

        super.spawn(player)
    }

    override fun onLeftClick(player: Player)
    {
        val damage = when (player.inventory.itemInHand.type)
        {
            XMaterial.WOODEN_SWORD.parseMaterial()!! -> 3.0
            XMaterial.GOLDEN_SWORD.parseMaterial()!! -> 3.0
            XMaterial.STONE_SWORD.parseMaterial()!! -> 4.0
            XMaterial.IRON_SWORD.parseMaterial()!! -> 5.0
            XMaterial.DIAMOND_SWORD.parseMaterial()!! -> 6.0
            else -> 2.0
        }

        attack(player, damage, false)
    }

    open fun attack(attacker: org.bukkit.entity.Entity, damage: Double, ignoreCooldown: Boolean = false): Boolean
    {
        if (!attackable || dead)
        {
            return false
        }

        if (!ignoreCooldown)
        {
            if (lastAttack != null && System.currentTimeMillis() < lastAttack!! + 500L)
            {
                return false
            }
        }

        if (attacker is Player)
        {
            val attackEvent = EntityDamageEvent(attacker, this, damage)
            attackEvent.call()

            if (attackEvent.isCancelled)
            {
                return false
            }
        }

        lastAttack = System.currentTimeMillis()
        health -= damage

        if (attacker is Player)
        {
            attacker.playSound(
                attacker.location,
                Sound.HURT_FLESH,
                1.0f,
                1.0f
            )
        }

        if (health <= 0.0)
        {
            kill(attacker)
            sendStatusPacket(3)

            Tasks.delayed(40L) {
                destroyForCurrentWatchers()
            }
        } else
        {
            sendAnimationPacket(1)
        }

        return true
    }

    open fun kill(killer: org.bukkit.entity.Entity)
    {
        dead = true

        EntityDeathEvent(killer, this).call()
    }

    internal fun tickEffects()
    {
        val iterator = effects.iterator()
        while (iterator.hasNext())
        {
            val effect = iterator.next()
            if (!effect.isPermanent() && effect.isExpired())
            {
                iterator.remove()
            } else
            {
                effect.effectType.tick(effect)
            }
        }

        val particleColor = findParticlesColor()
        val ambientEffect = effects.any { it.effectType.isAmbient() }

        if (particleColor > 0)
        {
            var spawnParticle = ThreadLocalRandom.current().nextBoolean()

            if (ambientEffect)
            {
                spawnParticle = spawnParticle and (ThreadLocalRandom.current().nextInt(5) == 0)
            }

            if (spawnParticle && particleColor > 0)
            {
                val colorR = ((particleColor shr 16 and 255).toDouble() / 255.0).toFloat()
                val colorG = ((particleColor shr 8 and 255).toDouble() / 255.0).toFloat()
                val colorB = ((particleColor shr 0 and 255).toDouble() / 255.0).toFloat()

                val particleType = if (ambientEffect)
                {
                    MinecraftReflection.getEnumParticle("SPELL_MOB_AMBIENT") ?:
                    Reflection
                        .getClassSuppressed(
                            "net.minecraft.core.particles.Particles"
                        )!!
                        .getDeclaredField("a")
                        .get(null)!!
                } else
                {
                    MinecraftReflection.getEnumParticle("SPELL_MOB") ?:
                    Reflection
                        .getClassSuppressed(
                            "net.minecraft.core.particles.Particles"
                        )!!
                        .getDeclaredField("b")
                        .get(null)!!
                }

                val x = location.z + (ThreadLocalRandom.current().nextDouble() - 0.5) * this.width
                val y = location.y + ThreadLocalRandom.current().nextDouble() * this.length
                val z = location.z + (ThreadLocalRandom.current().nextDouble() - 0.5) * this.width
                val loc = Location(location.world, x, y, z)

                ParticleUtil.sendsParticleToAll(
                    location.world,
                    ParticleMeta(loc, particleType, colorR, colorG, colorB, 1.0F, 1)
                )
            }
        }
    }

    private fun findParticlesColor(): Int
    {
        return if (effects.isEmpty())
        {
            3694022
        } else
        {
            var colorR = 0.0f
            var colorG = 0.0f
            var colorB = 0.0f
            var amplifier = 0

            val iterator = effects.iterator()
            while (iterator.hasNext())
            {
                val effect = iterator.next()
                if (effect.effectType.isShowParticles())
                {
                    val particleColor = effect.effectType.getParticlesColor()!!.asRGB()
                    val particleAmplifier = effect.effectType.getParticlesAmplifier() + 1

                    colorR += (particleAmplifier * (particleColor shr 16 and 255)).toFloat() / 255.0f
                    colorG += (particleAmplifier * (particleColor shr 8 and 255)).toFloat() / 255.0f
                    colorB += (particleAmplifier * (particleColor shr 0 and 255)).toFloat() / 255.0f
                    amplifier += particleAmplifier
                }
            }

            if (amplifier == 0)
            {
                0
            } else
            {
                colorR = colorR / amplifier.toFloat() * 255.0f
                colorG = colorG / amplifier.toFloat() * 255.0f
                colorB = colorB / amplifier.toFloat() * 255.0f
                colorR.toInt() shl 16 or (colorG.toInt() shl 8) or colorB.toInt()
            }
        }
    }

    open fun getHealthDisplay(): String
    {
        return if (!dead)
        {
            "${ChatColor.WHITE}${(health / 2.0).toInt()}${ChatColor.DARK_RED}${Constants.HEART_SYMBOL}"
        } else
        {
            "${ChatColor.GRAY}*DEAD*"
        }
    }

}

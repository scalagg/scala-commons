package net.evilblock.cubed.entity.npc.animation

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.entity.animation.EntityAnimationType
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.menus.SelectColorMenu
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.util.nms.particle.ParticleMeta
import net.evilblock.cubed.util.nms.particle.ParticleUtil
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class MultiRingsAnimation(entity: NpcEntity, private val color: Color) : EntityAnimation(entity) {

    companion object {
        private const val DIST = 0.8
    }

    private var ticks: Int = 0

    override fun getAnimationType(): EntityAnimationType {
        return MultiRingsAnimationType
    }

    override fun getInterval(): Long {
        return 50L
    }

    override fun tick() {
        ticks++

        val middleLocation = entity.location.clone()
        middleLocation.x = floor(middleLocation.x) + 0.5
        middleLocation.y = middleLocation.y + 1.0
        middleLocation.z = floor(middleLocation.z) + 0.5

        val yinAngle = (ticks + 1) * ((2 * Math.PI) / 80)
        val yangle = if (ticks + 1 >= 40) {
            (ticks - 40 + 1) * ((2 * Math.PI) / 80)
        } else {
            (ticks + 40 + 1) * ((2 * Math.PI) / 80)
        }

        val yin = middleLocation.clone().add(DIST * cos(yinAngle), ticks.toDouble(), DIST * sin(yinAngle))
        val yang = middleLocation.clone().add(DIST * cos(yangle), (-ticks).toDouble(), DIST * sin(yangle))

        val particles = listOf(
            ParticleMeta(
                yin,
                FloatAnimation.REDSTONE,
                color,
                3F,
                0
            ),
            ParticleMeta(
                yang,
                FloatAnimation.REDSTONE,
                color,
                3F,
                0
            )
        )

        ParticleUtil.sendsParticleToAll(entity.location.world, *particles.toTypedArray())

        if (ticks >= 80) {
            ticks = 0
        }
    }

    override fun getAbstractType(): Type {
        return MultiRingsAnimation::class.java
    }

    object MultiRingsAnimationType : EntityAnimationType {
        override fun getName(): String {
            return "Multi Rings"
        }

        override fun getIcon(): ItemStack {
            return ItemStack(Material.ENDER_PEARL)
        }

        override fun startProcedure(player: Player, entity: Entity, complete: (EntityAnimation) -> Unit) {
            SelectColorMenu { color ->
                complete.invoke(MultiRingsAnimation(entity as NpcEntity, color))
            }.openMenu(player)
        }
    }

}

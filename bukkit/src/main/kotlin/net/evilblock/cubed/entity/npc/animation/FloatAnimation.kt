package net.evilblock.cubed.entity.npc.animation

import net.evilblock.cubed.entity.Entity
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.entity.animation.EntityAnimationType
import net.evilblock.cubed.entity.npc.NpcEntity
import net.evilblock.cubed.menu.menus.SelectColorMenu
import net.evilblock.cubed.util.Reflection
import net.evilblock.cubed.util.nms.MinecraftReflection
import net.evilblock.cubed.util.nms.particle.ParticleMeta
import net.evilblock.cubed.util.nms.particle.ParticleUtil
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Type
import kotlin.random.Random

class FloatAnimation(entity: NpcEntity, private val color: Color) : EntityAnimation(entity)
{
    companion object
    {
        val REDSTONE = MinecraftReflection
            .getEnumParticle("REDSTONE")
            ?: Reflection.getClassSuppressed(
                "net.minecraft.core.particles.Particles"
            )!!.getDeclaredField(
                "o"
            ).get(null)!!
    }

    override fun tick()
    {
        val randomX = Random.nextDouble(entity.location.x - 0.4, entity.location.x + 0.4)
        val randomY = Random.nextDouble(entity.location.y - 0.2, entity.location.y + 0.2)
        val randomZ = Random.nextDouble(entity.location.z - 0.4, entity.location.z + 0.4)

        ParticleUtil.sendsParticleToAll(
            entity.location.world, ParticleMeta(
                Location(entity.location.world, randomX, randomY, randomZ),
                REDSTONE,
                color,
                1F,
                1
            )
        )
    }

    override fun getInterval(): Long
    {
        return 250
    }

    override fun getAnimationType(): EntityAnimationType
    {
        return FloatAnimationType
    }

    override fun getAbstractType(): Type
    {
        return FloatAnimation::class.java
    }

    object FloatAnimationType : EntityAnimationType
    {
        override fun getName(): String
        {
            return "Float"
        }

        override fun getIcon(): ItemStack
        {
            return ItemStack(Material.BLAZE_POWDER)
        }

        override fun startProcedure(player: Player, entity: Entity, complete: (EntityAnimation) -> Unit)
        {
            SelectColorMenu { color ->
                complete.invoke(FloatAnimation(entity as NpcEntity, color))
            }.openMenu(player)
        }
    }

}

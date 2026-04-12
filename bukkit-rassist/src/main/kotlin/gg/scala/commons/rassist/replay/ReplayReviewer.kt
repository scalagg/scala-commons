package gg.scala.commons.rassist.replay

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.event.PacketSendEvent
import gg.scala.commons.rassist.dao.Replay
import gg.scala.commons.rassist.dao.packet.ReplayPacketWrapper
import gg.scala.commons.rassist.packet.ReplayPacketBridge
import me.lucko.helper.Schedulers
import me.lucko.helper.scheduler.Task
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.entity.Player
import java.util.logging.Level
import kotlin.math.ceil
import kotlin.math.roundToInt

class ReplayReviewer(
    private val replay: Replay,
    private val reviewers: List<Player>
) : Runnable, PacketListenerAbstract(PacketListenerPriority.LOWEST)
{
    private var currentTick = 1.0
    private var currentPacket = 2

    private var speed = 1.0F
    private var playing = false

    fun updateSpeed(
        float: Float
    )
    {
        this.speed = float
    }

    private lateinit var task: Task
    private lateinit var terminable: CompositeTerminable

    fun start()
    {
        this.playing = true
        this.terminable =
            CompositeTerminable.create()

        PacketEvents.getAPI().eventManager.registerListener(this)

        this.task = Schedulers.sync()
            .runRepeating(
                this, 0L, 1L
            )
    }

    fun skip(ticks: Int)
    {
        val newTicks = (getDurationTicks() - currentTick)
            .roundToInt().coerceAtMost(ticks)

        if (newTicks > 0)
        {
            this.executeTick(this.currentTick + newTicks)
            this.currentTick += newTicks.toDouble()
        }

        this.updateXpBar()
    }

    fun stop()
    {
        PacketEvents.getAPI().eventManager.unregisterListener(this)
        this.terminable
            .closeAndReportException()

        this.task.stop()
    }

    fun reset()
    {
        currentPacket = 2
        currentTick = 1.0
        updateXpBar()
    }

    override fun run()
    {
        if (this.playing)
        {
            this.executeTick(currentTick)

            if (this.currentTick < this.getDurationTicks())
            {
                if (currentTick % 20 == 0.0)
                {
                    this.updateXpBar()
                }

                this.currentTick += this.speed

                if (this.currentTick == this.getDurationTicks())
                {
                    this.updateXpBar()
                }
            }
        }
    }

    private fun executeTick(tick: Double)
    {
        var packet: ReplayPacketWrapper?

        while (
            replay.getPacket(currentPacket)
                .also { packet = it } != null &&
            packet!!.time <= tick * 50
        )
        {
            for (reviewer in this.reviewers)
            {
                kotlin.runCatching {
                    PacketEvents.getAPI().playerManager.sendPacket(reviewer, packet!!.toByteBuf())
                }.onFailure {
                    ScalaCommonsSpigot.instance.logger
                        .log(Level.WARNING, "something went wong", it)
                }
            }

            this.currentPacket++
        }
    }

    private fun updateXpBar()
    {
        this.reviewers.forEach {
            it.level = (this.currentTick / 20).roundToInt()
            it.exp = (this.currentTick / getDurationTicks()).toFloat()
        }
    }

    private fun getDurationTicks(): Double
    {
        return ceil((this.replay.metadata.timestamps.duration.toFloat() / 50).toDouble())
    }

    override fun onPacketSend(event: PacketSendEvent)
    {
        if (!playing) {
            return
        }

        val player = event.getPlayer<Player>() ?: return
        if (reviewers.any { it.uniqueId == player.uniqueId } && ReplayPacketBridge.packetTypesInternalPE.contains(event.packetType)) {
            event.isCancelled = true
        }
    }
}

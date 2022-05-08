package gg.scala.commons.rassist.replay

import gg.scala.commons.rassist.dao.Replay
import gg.scala.commons.rassist.dao.packet.ReplayPacketWrapper
import me.lucko.helper.protocol.Protocol
import org.bukkit.entity.Player
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
class ReplayReviewer(
    private val replay: Replay,
    private val reviewers: List<Player>
) : Runnable
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

    fun start()
    {
        this.playing = true
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

    fun reset()
    {
        currentPacket = 3
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
        var packet: List<ReplayPacketWrapper>

        while (
            replay.getPacket(currentPacket)
                .also { packet = it }
                .isNotEmpty() &&
            packet[0].time <= tick * 50
        )
        {
            for (reviewer in this.reviewers)
            {
                if (!reviewer.isOnline)
                    continue

                kotlin.runCatching {
                    for (wrapper in packet)
                    {
                        Protocol.manager()
                            .sendWirePacket(
                                reviewer, wrapper
                            )
                    }
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
}

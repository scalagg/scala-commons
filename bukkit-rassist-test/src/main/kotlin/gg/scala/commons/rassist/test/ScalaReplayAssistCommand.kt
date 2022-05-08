package gg.scala.commons.rassist.test

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.rassist.dao.Replay
import gg.scala.commons.rassist.recorder.Recorder
import gg.scala.commons.rassist.replay.ReplayReviewer
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author GrowlyX
 * @since 5/8/2022
 */
@AutoRegister
object ScalaReplayAssistCommand : ScalaCommand()
{
    private var recording: Recorder? = null
    private var replay: Replay? = null
    private var reviewer: ReplayReviewer? = null

    @CommandAlias("stop-replay")
    fun onStop(player: Player)
    {
        this.reviewer!!.stop()

        player.sendMessage("We've stopped your replay review")
    }

    @CommandAlias("start-replay")
    fun onReplay(player: Player)
    {
        val viewer = ReplayReviewer(
            replay!!, listOf(player)
        )

        this.reviewer = viewer

        viewer.start()

        Events.subscribe(PlayerInteractEvent::class.java)
            .handler {

            }
    }

    @CommandAlias("start-recording")
    fun onStart(player: Player)
    {
        val recorder = Recorder(
            UUID.randomUUID(),
            listOf(player.uniqueId)
        )

        recorder.initialize()
        recorder.start()

        recording = recorder

        player.sendMessage("${CC.GREEN}We've started your recording")
    }

    @CommandAlias("end-recording")
    fun onEnd(player: Player): CompletableFuture<Void>
    {
        player.sendMessage(
            "${CC.GREEN}We've stopping your recording"
        )

        return recording!!.stopAndSave()
            .thenAccept {
                this.replay = it

                player.sendMessage("${CC.GREEN}Stopped your recording")
            }
    }
}

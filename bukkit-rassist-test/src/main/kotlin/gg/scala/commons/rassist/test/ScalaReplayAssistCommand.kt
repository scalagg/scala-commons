package gg.scala.commons.rassist.test

import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.rassist.dao.Replay
import gg.scala.commons.rassist.recorder.Recorder
import gg.scala.commons.rassist.replay.ReplayReviewer
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterOutputStream

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
    fun onReplay(player: Player, float: Float)
    {
        val viewer = ReplayReviewer(
            replay!!, listOf(player)
        )

        this.reviewer = viewer

        viewer.updateSpeed(float)
        viewer.start()
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

                val start = System.currentTimeMillis()

                val serialized = Serializers
                    .gson.toJson(it)
                    .encodeToByteArray()

                val stop = System.currentTimeMillis()

                val outputStream = ByteArrayOutputStream()
                val deflate = DeflaterOutputStream(outputStream)

                deflate.write(serialized)
                deflate.flush()
                deflate.close()

                val output = outputStream
                    .toByteArray()

                val newSTop = System.currentTimeMillis()

                player.sendMessage("${CC.D_AQUA}A ${
                    it.metadata.timestamps.duration
                }ms (${
                    it.metadata.timestamps.duration / 1000
                }s) long replay:")
                player.sendMessage("")

                player.sendMessage(
                    "${CC.GREEN}This replay is: ${
                        CC.WHITE
                    }${
                        serialized.size / 1000
                    }kb, or ${
                        serialized.size / 1000000
                    }mb in size"
                )

                player.sendMessage("")

                player.sendMessage(
                    "${CC.GREEN}The compressed replay is: ${
                        CC.WHITE
                    }${
                        output.size / 1000
                    }kb, or ${
                        output.size / 1000000
                    }mb in size"
                )


                val outputStreamInfl = ByteArrayOutputStream()
                val inflator = InflaterOutputStream(outputStreamInfl)

                inflator.write(output)
                inflator.flush()
                inflator.close()

                val inflated = outputStreamInfl
                    .toByteArray()

                player.sendMessage(
                    "${CC.GREEN}The decompressed replay is: ${
                        CC.WHITE
                    }${
                        inflated.size / 1000
                    }kb, or ${
                        inflated.size / 1000000
                    }mb in size"
                )

                player.sendMessage("")

                player.sendMessage(
                    "${CC.GREEN}It took ${CC.D_AQUA}${
                        stop - start
                    }ms${CC.GREEN} to serialize this replay"
                )

                player.sendMessage(
                    "${CC.GREEN}It took ${CC.D_AQUA}${
                        newSTop - stop
                    }ms${CC.GREEN} to compress this replay"
                )

                player.sendMessage("${CC.GREEN}Stopped your recording")
            }
    }
}

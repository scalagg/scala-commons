package gg.scala.commons.rassist.dao.packet

import com.github.retrooper.packetevents.event.ProtocolPacketEvent
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

class ReplayPacketWrapper(
    val time: Int,
    val packetId: Int,
    val data: ByteArray
)
{
    fun toByteBuf(): ByteBuf
    {
        val idBytes = gg.scala.commons.rassist.util.ReplayPacketUtils.toVarInt(packetId)
        return Unpooled.wrappedBuffer(idBytes, data)
    }

    companion object
    {
        fun fromPacketEvent(time: Int, event: ProtocolPacketEvent): ReplayPacketWrapper
        {
            val byteBuf = event.byteBuf as ByteBuf
            val duplicate = byteBuf.duplicate()
            val bytes = ByteArray(duplicate.readableBytes())
            duplicate.readBytes(bytes)
            return ReplayPacketWrapper(time, event.packetId, bytes)
        }
    }
}

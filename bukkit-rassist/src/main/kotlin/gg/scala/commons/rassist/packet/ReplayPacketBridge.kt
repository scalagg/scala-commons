package gg.scala.commons.rassist.packet

import com.comphenix.protocol.PacketType
import gg.scala.commons.rassist.dao.packet.versions.ReplayPacketConverterLegacyImp

/**
 * @author GrowlyX
 * @since 5/7/2022
 */
object ReplayPacketBridge
{
    val packetTypesInternal = listOf(
        PacketType.Play.Client.ARM_ANIMATION,
        PacketType.Play.Server.REL_ENTITY_MOVE,
        PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_LOOK,
        PacketType.Play.Server.MAP_CHUNK, PacketType.Play.Server.MAP_CHUNK_BULK,
        PacketType.Play.Server.LIGHT_UPDATE, PacketType.Play.Server.WORLD_BORDER,
        PacketType.Play.Server.WORLD_PARTICLES, PacketType.Play.Server.WORLD_EVENT,
        PacketType.Play.Server.NAMED_SOUND_EFFECT, PacketType.Play.Server.LOOK_AT,
        PacketType.Play.Server.VIEW_CENTRE, PacketType.Play.Server.UPDATE_TIME,
        PacketType.Play.Server.COLLECT, PacketType.Play.Server.NAMED_ENTITY_SPAWN,
        PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB,
        PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.SPAWN_ENTITY_WEATHER,
        PacketType.Play.Server.SPAWN_ENTITY_PAINTING, PacketType.Play.Server.ENTITY,
        PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.ENTITY_TELEPORT,
        PacketType.Play.Server.ENTITY_STATUS, PacketType.Play.Server.ATTACH_ENTITY,
        PacketType.Play.Server.ENTITY_EFFECT, PacketType.Play.Server.REMOVE_ENTITY_EFFECT,
        PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.EXPLOSION,
        PacketType.Play.Server.ENTITY_METADATA, PacketType.Play.Server.ENTITY_DESTROY,
        PacketType.Play.Server.ENTITY_SOUND, PacketType.Play.Server.UNLOAD_CHUNK,
        PacketType.Play.Server.MULTI_BLOCK_CHANGE, PacketType.Play.Server.BLOCK_CHANGE,
        PacketType.Play.Server.BLOCK_ACTION, PacketType.Play.Server.UPDATE_SIGN,
        PacketType.Play.Server.BLOCK_BREAK_ANIMATION, PacketType.Play.Server.GAME_STATE_CHANGE
    ).filter {
        it.isSupported
    }

    private val converter =
        ReplayPacketConverterLegacyImp()

    fun converter() = converter
}

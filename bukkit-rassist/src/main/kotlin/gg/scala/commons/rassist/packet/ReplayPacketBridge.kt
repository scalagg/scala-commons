package gg.scala.commons.rassist.packet

import com.github.retrooper.packetevents.protocol.packettype.PacketType as PEPacketType

object ReplayPacketBridge
{
    val packetTypesInternalPE = listOf(
        PEPacketType.Play.Client.ANIMATION,
        PEPacketType.Play.Server.ENTITY_RELATIVE_MOVE,
        PEPacketType.Play.Server.ENTITY_RELATIVE_MOVE_AND_ROTATION,
        PEPacketType.Play.Server.ENTITY_ROTATION,
        PEPacketType.Play.Server.CHUNK_DATA,
        PEPacketType.Play.Server.MAP_CHUNK_BULK,
        PEPacketType.Play.Server.UPDATE_LIGHT,
        PEPacketType.Play.Server.WORLD_BORDER,
        PEPacketType.Play.Server.PARTICLE,
        PEPacketType.Play.Server.EFFECT,
        PEPacketType.Play.Server.SOUND_EFFECT,
        PEPacketType.Play.Server.FACE_PLAYER,
        PEPacketType.Play.Server.UPDATE_VIEW_POSITION,
        PEPacketType.Play.Server.TIME_UPDATE,
        PEPacketType.Play.Server.COLLECT_ITEM,
        PEPacketType.Play.Server.SPAWN_PLAYER,
        PEPacketType.Play.Server.SPAWN_ENTITY,
        PEPacketType.Play.Server.SPAWN_EXPERIENCE_ORB,
        PEPacketType.Play.Server.SPAWN_LIVING_ENTITY,
        PEPacketType.Play.Server.SPAWN_WEATHER_ENTITY,
        PEPacketType.Play.Server.SPAWN_PAINTING,
        PEPacketType.Play.Server.ENTITY_MOVEMENT,
        PEPacketType.Play.Server.ENTITY_VELOCITY,
        PEPacketType.Play.Server.ENTITY_TELEPORT,
        PEPacketType.Play.Server.ENTITY_STATUS,
        PEPacketType.Play.Server.ATTACH_ENTITY,
        PEPacketType.Play.Server.ENTITY_EFFECT,
        PEPacketType.Play.Server.REMOVE_ENTITY_EFFECT,
        PEPacketType.Play.Server.ENTITY_EQUIPMENT,
        PEPacketType.Play.Server.EXPLOSION,
        PEPacketType.Play.Server.ENTITY_METADATA,
        PEPacketType.Play.Server.DESTROY_ENTITIES,
        PEPacketType.Play.Server.ENTITY_SOUND_EFFECT,
        PEPacketType.Play.Server.UNLOAD_CHUNK,
        PEPacketType.Play.Server.MULTI_BLOCK_CHANGE,
        PEPacketType.Play.Server.BLOCK_CHANGE,
        PEPacketType.Play.Server.BLOCK_ACTION,
        PEPacketType.Play.Server.UPDATE_SIGN,
        PEPacketType.Play.Server.BLOCK_BREAK_ANIMATION,
        PEPacketType.Play.Server.CHANGE_GAME_STATE,
    ).filterNotNull()
}

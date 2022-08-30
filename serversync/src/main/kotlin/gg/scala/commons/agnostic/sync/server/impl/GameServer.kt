package gg.scala.commons.agnostic.sync.server.impl

import gg.scala.commons.agnostic.sync.server.AbstractServer
import gg.scala.commons.agnostic.sync.server.region.Region
import java.util.UUID

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
class GameServer(
    override val id: String,
    override val groups: List<String>,
    override val region: Region
) : AbstractServer()
{
    override fun getAbstractType() = GameServer::class.java

    fun getTPS() = this
        .getMetadataValue<Double>(
            "server", "tps"
        )

    fun getCpuUsage() = this
        .getMetadataValue<Double>(
            "server", "cpu"
        )

    fun getPlayersCount() = this
        .getMetadataValue<Int>(
            "server", "online"
        )

    fun getPlayers() = this
        .getMetadataValue<List<UUID>>(
            "server", "online-list"
        )

    fun getMaxPlayers() = this
        .getMetadataValue<Int>(
            "server", "online-max"
        )

    fun getSoftware() = this
        .getMetadataValue<String>(
            "server", "software"
        )

    fun getWhitelisted() = this
        .getMetadataValue<Boolean>(
            "server", "whitelisted"
        )

    fun getWhitelistedPlayers() = this
        .getMetadataValue<List<UUID>>(
            "server", "whitelisted-players"
        )
}

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

    fun getHost() = this
        .getMetadataValue<String>(
            "server", "host"
        )

    fun getPort() = this
        .getMetadataValue<Int>(
            "server", "port"
        )

    fun getTPS() = this
        .getMetadataValue<Double>(
            "server", "tps"
        )

    fun getCpuUsage() = this
        .getMetadataValue<Double>(
            "server", "cpu"
        )

    fun getPlayers() = this
        .getMetadataValue<List<UUID>>(
            "server", "online-list"
        )

    fun getPlayersCount() = this
        .getMetadataValue<Double>(
            "server", "online"
        )
        ?.toInt()

    fun getMaxPlayers() = this
        .getMetadataValue<Double>(
            "server", "online-max"
        )
        ?.toInt()

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

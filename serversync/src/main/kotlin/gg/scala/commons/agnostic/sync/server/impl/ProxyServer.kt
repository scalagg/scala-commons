package gg.scala.commons.agnostic.sync.server.impl

import gg.scala.commons.agnostic.sync.server.AbstractServer
import gg.scala.commons.agnostic.sync.server.region.Region
import java.lang.reflect.Type

class ProxyServer(
    override val id: String,
    override val groups: List<String>,
    override val region: Region
) : AbstractServer()
{
    override fun getAbstractType() = ProxyServer::class.java

    fun getPlayersCount() = this
        .getMetadataValue<Int>(
            "velocity", "online"
        )

    fun getSoftware() = this
        .getMetadataValue<String>(
            "velocity", "software"
        )
}

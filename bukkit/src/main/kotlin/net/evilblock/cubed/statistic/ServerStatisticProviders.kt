package net.evilblock.cubed.statistic

import net.evilblock.cubed.statistic.impl.DefaultServerStatisticProvider

object ServerStatisticProviders
{
    var current: ServerStatisticProvider = DefaultServerStatisticProvider

    fun provider() = this.current
}

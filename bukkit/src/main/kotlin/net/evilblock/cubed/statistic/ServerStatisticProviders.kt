package net.evilblock.cubed.statistic

import net.evilblock.cubed.statistic.impl.DefaultServerStatisticProvider

/**
 * @author GrowlyX
 * @since 8/30/2022
 */
object ServerStatisticProviders
{
    var current: ServerStatisticProvider = DefaultServerStatisticProvider

    fun provider() = this.current
}

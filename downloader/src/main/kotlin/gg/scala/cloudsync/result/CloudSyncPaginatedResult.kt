package gg.scala.cloudsync.result

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.PaginatedResult

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
object CloudSyncPaginatedResult : PaginatedResult<String>()
{
    override fun format(result: String, resultIndex: Int): String
    {
        val split = result.split(":")
        return "${CC.WHITE}$resultIndex. ${CC.SEC}(Group: ${CC.GREEN}${split[0]}${CC.SEC}, Module: ${CC.LIGHT_PURPLE}${split[1]}${CC.SEC}, Archive: ${CC.RED}${split[2]}${CC.SEC})"
    }

    override fun getHeader(page: Int, maxPages: Int) =
        "${CC.PRI}=== ${CC.SEC}Tracked components ${CC.GRAY}($page/$maxPages) ${CC.PRI}==="
}

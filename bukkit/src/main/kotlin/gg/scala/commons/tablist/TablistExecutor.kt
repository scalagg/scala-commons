package gg.scala.commons.tablist

import gg.scala.commons.tablist.adapter.TabElementProxy
import gg.scala.commons.tablist.manage.TablistManager

object TablistExecutor : Runnable
{
    private val proxy = TabElementProxy()

    override fun run()
    {
        runCatching(::caughtRun)
            .onFailure {
                it.printStackTrace()
            }
    }

    fun caughtRun()
    {
        for (handle in TablistManager.get().tablistHandles)
        {
            proxy.setActiveHandle(handle)
            TablistService.populator?.populate(handle.holder, proxy)
            handle.onTick()
        }
    }
}

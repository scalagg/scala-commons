package gg.scala.commons.scripts.binding

import gg.scala.commons.scripts.ScriptPluginService

/**
 * @author GrowlyX
 * @since 4/22/2022
 */
class Scripts(
    private val internalService: ScriptPluginService
)
{
    fun internal() = internalService
}

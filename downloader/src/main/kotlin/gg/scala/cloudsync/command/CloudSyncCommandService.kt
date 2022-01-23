package gg.scala.cloudsync.command

import gg.scala.cloudsync.ScalaCloudSync
import gg.scala.cloudsync.command.commands.CloudSyncCommand
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.command.manager.CubedCommandManager

/**
 * @author GrowlyX
 * @since 1/22/2022
 */
@Service
object CloudSyncCommandService
{
    @Inject
    lateinit var plugin: ScalaCloudSync

    @Configure
    fun configure()
    {
        val manager = CubedCommandManager(plugin)
        manager.registerCommand(CloudSyncCommand)
    }
}

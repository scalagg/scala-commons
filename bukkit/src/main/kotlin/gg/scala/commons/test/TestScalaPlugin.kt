package gg.scala.commons.test

import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.LibraryLoader
import gg.scala.commons.annotations.Listeners
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.command.ScalaCommand
import gg.scala.commons.library.model.Library
import me.lucko.helper.plugin.ap.Plugin
import net.evilblock.cubed.command.manager.CubedCommandManager
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * @author GrowlyX
 * @since 4/14/2022
 */
//@Plugin(
//    name = "test"
//)
class TestScalaPlugin : ExtendedScalaPlugin()
{
    @ContainerEnable
    fun containerEnable()
    {
        logger.info("hi")
    }
}

object TestCustomizer
{
    @CommandManagerCustomizer
    fun customize(manager: CubedCommandManager)
    {
        manager.enableUnstableAPI("help")
    }
}

@Listeners
object TestListener : Listener
{
    @EventHandler
    fun onLogin(login: PlayerJoinEvent)
    {
        login.joinMessage = ""
    }
}

@Library(
    groupId = "test.test",
    artifactId = "test-lol",
    version = "1.0.0"
)
@LibraryLoader
object TestLibraryLoader

@AutoRegister
@CommandAlias("test")
class TestCommand : ScalaCommand()
{
    @Default
    fun onDefault(sender: CommandSender)
    {
        sender.sendMessage("hi")
    }
}

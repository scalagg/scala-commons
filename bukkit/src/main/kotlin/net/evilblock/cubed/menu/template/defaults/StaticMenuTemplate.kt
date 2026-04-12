

package net.evilblock.cubed.menu.template.defaults

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateFactory
import org.bukkit.ChatColor
import org.bukkit.Material
import java.lang.reflect.Type

class StaticMenuTemplate(id: String) : MenuTemplate<Any>(id) {

    override fun getListEntries(): List<Any> {
        return emptyList()
    }

    override fun getAbstractType(): Type {
        return StaticMenuTemplate::class.java
    }

    override fun createEntryButton(entry: Any): Button {
        return Button.placeholder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()!!, 14, "${ChatColor.DARK_RED}STATIC ENTRY BUTTON")
    }

    object StaticMenuTemplateFactory : MenuTemplateFactory {
        override fun getName(): String {
            return "Static"
        }

        override fun startProcedure(id: String, finish: (MenuTemplate<*>) -> Unit) {
            finish.invoke(StaticMenuTemplate(id))
        }
    }

}

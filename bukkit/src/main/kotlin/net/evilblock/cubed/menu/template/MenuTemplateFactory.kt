

package net.evilblock.cubed.menu.template

interface MenuTemplateFactory {

    fun getName(): String

    fun startProcedure(id: String, finish: (MenuTemplate<*>) -> Unit)

}
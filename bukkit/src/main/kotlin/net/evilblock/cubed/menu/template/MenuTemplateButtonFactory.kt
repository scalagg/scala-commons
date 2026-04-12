

package net.evilblock.cubed.menu.template

interface MenuTemplateButtonFactory {

    fun getName(): String

    fun createButton(): MenuTemplateButton

}
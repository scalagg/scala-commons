/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template

import com.google.common.base.Charsets
import com.google.gson.reflect.TypeToken
import gg.scala.commons.util.Files
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.ScalaCommonsSpigot
import net.evilblock.cubed.menu.template.defaults.StaticMenuTemplate
import net.evilblock.cubed.serializers.Serializers
import java.io.File

@Service
object MenuTemplateHandler
{
    @Inject
    lateinit var plugin: ScalaCommonsSpigot

    private val DATA_TYPE = object : TypeToken<List<MenuTemplate<*>>>()
    {}.type

    private val templates: MutableMap<String, MenuTemplate<*>> = hashMapOf()
    private val factories: MutableList<MenuTemplateFactory> = arrayListOf(StaticMenuTemplate.StaticMenuTemplateFactory)

    private fun getDataFile(): File
    {
        return File(plugin.dataFolder, "menu-templates.json")
    }

    @Configure
    fun configure()
    {
        if (getDataFile().exists())
        {
            Files.read(getDataFile()){ reader ->
                val list = Serializers.gson.fromJson(reader, DATA_TYPE) as List<MenuTemplate<*>>
                for (template in list)
                {
                    templates[template.id.toLowerCase()] = template
                }
            }
        }
    }

    @Close
    fun close()
    {
        Files.write(Serializers.gson.toJson(templates.values, DATA_TYPE), getDataFile())
    }

    @JvmStatic
    fun getTemplates(): List<MenuTemplate<*>>
    {
        return templates.values.toList()
    }

    @JvmStatic
    fun findTemplate(id: String): MenuTemplate<*>?
    {
        return templates[id.toLowerCase()]
    }

    @JvmStatic
    fun trackTemplate(template: MenuTemplate<*>)
    {
        templates[template.id.toLowerCase()] = template
    }

    @JvmStatic
    fun forgetTemplate(template: MenuTemplate<*>)
    {
        templates.remove(template.id.toLowerCase())
    }

    @JvmStatic
    fun getFactories(): List<MenuTemplateFactory>
    {
        return factories.toList()
    }

    @JvmStatic
    fun registerFactory(factory: MenuTemplateFactory)
    {
        factories.add(factory)
    }

}

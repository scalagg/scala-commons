/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.menu.template

import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.Cubed
import net.evilblock.cubed.menu.template.defaults.StaticMenuTemplate
import net.evilblock.cubed.serializers.Serializers
import java.io.File

object MenuTemplateHandler {

    private val DATA_TYPE = object : TypeToken<List<MenuTemplate<*>>>() {}.type

    private val templates: MutableMap<String, MenuTemplate<*>> = hashMapOf()
    private val factories: MutableList<MenuTemplateFactory> = arrayListOf(StaticMenuTemplate.StaticMenuTemplateFactory)

    private fun getDataFile(): File {
        return File(Cubed.instance.dataFolder, "menu-templates.json")
    }

    @JvmStatic
    fun initialLoad() {
        if (getDataFile().exists()) {
            Files.newReader(getDataFile(), Charsets.UTF_8).use { reader ->
                val list = Serializers.gson.fromJson(reader, DATA_TYPE) as List<MenuTemplate<*>>
                for (template in list) {
                    templates[template.id.toLowerCase()] = template
                }
            }
        }
    }

    @JvmStatic
    fun saveData() {
        Files.write(Serializers.gson.toJson(templates.values, DATA_TYPE), getDataFile(), Charsets.UTF_8)
    }

    @JvmStatic
    fun getTemplates(): List<MenuTemplate<*>> {
        return templates.values.toList()
    }

    @JvmStatic
    fun findTemplate(id: String): MenuTemplate<*>? {
        return templates[id.toLowerCase()]
    }

    @JvmStatic
    fun trackTemplate(template: MenuTemplate<*>) {
        templates[template.id.toLowerCase()] = template
    }

    @JvmStatic
    fun forgetTemplate(template: MenuTemplate<*>) {
        templates.remove(template.id.toLowerCase())
    }

    @JvmStatic
    fun getFactories(): List<MenuTemplateFactory> {
        return factories.toList()
    }

    @JvmStatic
    fun registerFactory(factory: MenuTemplateFactory) {
        factories.add(factory)
    }

}
package gg.scala.commons.localization

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

enum class LocalizationLoader(
    val load: (File) -> Map<String, Any>,
    val loadReader: (InputStreamReader) -> Map<String, Any>,
    val save: (Map<String, Any>, File) -> Unit,
    val fileExtension: String
)
{
    YAML(file@{
        val yaml = YamlConfiguration()
        yaml.load(it)

        return@file mutableMapOf<String, Any>()
            .apply {
                val keys = yaml.getKeys(true)

                keys.forEach { key ->
                    val value = yaml
                        .get(key)

                    this[key] = value.toString()
                }
            }
    }, reader@{
        val yaml = YamlConfiguration()
        yaml.load(it)

        return@reader mutableMapOf<String, Any>()
            .apply {
                val keys = yaml.getKeys(true)

                keys.forEach { key ->
                    val value = yaml
                        .get(key)

                    this[key] = value.toString()
                }
            }
    }, { entries, file ->
        val yaml = YamlConfiguration()
        entries.forEach {
            yaml.set(it.key, it.value)
        }

        yaml.save(file)
    }, "yml")
}

package gg.scala.commons.localize

import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.InputStreamReader

/**
 * @author GrowlyX
 * @since 3/30/2022
 */
enum class LocalizationLoader(
    val load: (File) -> Map<String, String>,
    val loadReader: (InputStreamReader) -> Map<String, String>,
    val save: (Map<String, String>, File) -> Unit,
    val fileExtension: String
)
{
    YAML(file@{
        val yaml = YamlConfiguration()
        yaml.load(it)

        return@file mutableMapOf<String, String>()
            .apply {
                val keys = yaml.getKeys(true)

                keys.forEach { key ->
                    val value = yaml
                        .getString(key)

                    this[key] = value
                }
            }
    }, reader@{
        val yaml = YamlConfiguration()
        yaml.load(it)

        return@reader mutableMapOf<String, String>()
            .apply {
                val keys = yaml.getKeys(true)

                keys.forEach { key ->
                    val value = yaml
                        .getString(key)

                    this[key] = value
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

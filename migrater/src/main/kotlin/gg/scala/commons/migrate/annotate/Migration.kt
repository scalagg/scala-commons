package gg.scala.commons.migrate.annotate

/**
 * @author GrowlyX
 * @since 9/11/2022
 */
annotation class Migration(
    val id: Int,
    val author: String,
    val description: String
)

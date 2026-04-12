package gg.scala.commons.migrate.annotate

annotation class Migration(
    val id: Int,
    val author: String,
    val description: String
)

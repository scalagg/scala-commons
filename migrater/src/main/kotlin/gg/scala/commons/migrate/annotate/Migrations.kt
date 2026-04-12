package gg.scala.commons.migrate.annotate

@Target(AnnotationTarget.CLASS)
annotation class Migrations(
    val value: String
)

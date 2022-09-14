package gg.scala.commons.migrate.annotate

/**
 * @author GrowlyX
 * @since 9/11/2022
 */
@Target(AnnotationTarget.CLASS)
annotation class Migrations(
    val value: String
)

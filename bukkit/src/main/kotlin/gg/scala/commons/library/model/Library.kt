package gg.scala.commons.library.model

/**
 * @author GrowlyX
 * @since 11/19/2021
 */
@Repeatable
annotation class Library(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val repository: String = "https://repo1.maven.org/maven2"
)

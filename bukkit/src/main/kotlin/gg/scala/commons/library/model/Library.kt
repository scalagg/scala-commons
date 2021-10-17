package gg.scala.commons.library.model

import gg.scala.commons.library.HelperLibraryHandler
import me.lucko.helper.maven.MavenLibrary
import java.net.MalformedURLException
import java.net.URL

/**
 * An object form of the [MavenLibrary]
 * annotation in helper. Should only be
 * used for [HelperLibraryHandler]
 * dependency processing.
 *
 * @author GrowlyX
 * @since 10/14/2021
 */
data class Library(
    val groupId: String,
    val artifactId: String,
    val version: String,
)
{
    var forcedRepository: String? = null

    fun repository(url: String): Library
    {
        forcedRepository = url; return this
    }

    fun mavenCentral(): Library
    {
        forcedRepository = "https://repo1.maven.org/maven2/"; return this
    }

    @Throws(MalformedURLException::class)
    fun getUrl(repoUrl: String): URL
    {
        var repo: String = repoUrl

        if (!repo.endsWith("/"))
        {
            repo += "/"
        }

        repo += "%s/%s/%s/%s-%s.jar"
        val url = String.format(repo, groupId.replace(".", "/"), artifactId, version, artifactId, version)
        return URL(url)
    }
}

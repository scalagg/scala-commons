package net.evilblock.cubed.library

import gg.scala.commons.annotations.LibraryLoader
import gg.scala.commons.library.model.Library

/**
 * @author GrowlyX
 * @since 7/30/2022
 */
@Library(
    groupId = "io.reactivex.rxjava3",
    artifactId = "rxjava",
    version = "3.1.5"
)
@Library(
    groupId = "io.sentry",
    artifactId = "sentry",
    version = "6.4.1"
)
@Library(
    groupId = "io.github.classgraph",
    artifactId = "classgraph",
    version = "4.8.149"
)
@Library(
    groupId = "org.apache.commons",
    artifactId = "commons-lang3",
    version = "3.12.0"
)
@Library(
    groupId = "com.google.zxing",
    artifactId = "core",
    version = "3.5.0"
)
@Library(
    groupId = "com.viaversion",
    artifactId = "viaversion-api",
    version = "4.3.1",
    repository = "https://repo.viaversion.com"
)
@Library(
    groupId = "me.lucko",
    artifactId = "scriptcontroller",
    version = "1.0",
    repository = "https://repo.lucko.me"
)
@LibraryLoader
object ScalaCommonsLibraries

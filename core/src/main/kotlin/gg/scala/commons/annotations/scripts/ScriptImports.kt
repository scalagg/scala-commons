package gg.scala.commons.annotations.scripts

@Target(
    AnnotationTarget.CLASS
)
annotation class ScriptImports(
    val value: Array<String>
)

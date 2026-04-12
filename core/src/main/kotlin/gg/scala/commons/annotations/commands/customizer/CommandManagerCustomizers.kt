package gg.scala.commons.annotations.commands.customizer

object CommandManagerCustomizers
{
    val customizers = mutableListOf<Class<*>>()

    inline fun <reified T> default()
    {
        customizers.add(T::class.java)
    }
}

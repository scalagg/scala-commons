package gg.scala.commons.annotations.commands.customizer

/**
 * @author GrowlyX
 * @since 4/14/2022
 */
object CommandManagerCustomizers
{
    val customizers = mutableListOf<Class<*>>()

    inline fun <reified T> default()
    {
        this.customizers.add(T::class.java)
    }
}

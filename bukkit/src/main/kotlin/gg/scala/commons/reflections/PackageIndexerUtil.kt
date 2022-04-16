package gg.scala.commons.reflections

import gg.scala.commons.instrumentation.Instrumental

/**
 * @author GrowlyX
 * @since 4/15/2022
 */
object PackageIndexerUtil
{
    fun getLoadedClasses(): List<Class<*>>
    {
        val classLoader = Thread
            .currentThread()
            .contextClassLoader

        return Instrumental
            .instrumentation()
            .getInitiatedClasses(classLoader)
            .toList()
    }

    inline fun <reified T> classesBySupertype(
        vararg exclude: Class<*>
    ): List<Class<*>>
    {
        return this.getLoadedClasses()
            .filter {
                !exclude.contains(it) && T::class.java.isAssignableFrom(it) && it != T::class.java
            }
    }

    fun classesInPackage(
        name: String
    ): List<Class<*>>
    {
        return this.getLoadedClasses()
            .filter {
                it.`package`.name.equals(name)
            }
    }
}

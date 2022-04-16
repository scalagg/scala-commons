package gg.scala.commons

import org.reflections.Store
import org.reflections.scanners.Scanners
import org.reflections.util.QueryFunction
import java.lang.reflect.Method

/**
 * @author GrowlyX
 * @since 4/16/2022
 */
object ScalaCommonsConstants
{
    inline fun <reified T> annotated(): QueryFunction<Store, Method>
    {
        return Scanners.MethodsAnnotated
            .with(T::class.java)
            .`as`(Method::class.java)
    }
}

package gg.scala.commons.annotations.custom

import kotlin.reflect.KClass

object CustomAnnotationProcessors
{
    val processors =
        mutableMapOf<KClass<out Annotation>, (Any) -> Unit>()

    inline fun <reified T : Annotation> process(
        noinline lambda: (Any) -> Unit
    )
    {
        processors[T::class] = lambda
    }
}

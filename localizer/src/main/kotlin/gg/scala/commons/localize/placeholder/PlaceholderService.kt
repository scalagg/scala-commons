package gg.scala.commons.localize.placeholder

object PlaceholderService
{
    private var processor: PlaceholderProcessor? = null

    fun processor() = processor

    internal fun register(processor: PlaceholderProcessor)
    {
        this.processor = processor
    }
}

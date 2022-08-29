package gg.scala.commons.agnostic.sync.server.metadata

/**
 * @author GrowlyX
 * @since 8/29/2022
 */
data class MetadataValue(
    var value: Any
)
{
    inline fun <reified T> cast() =
        this.value as T
}

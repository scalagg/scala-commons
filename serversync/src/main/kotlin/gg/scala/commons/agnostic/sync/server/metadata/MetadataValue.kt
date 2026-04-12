package gg.scala.commons.agnostic.sync.server.metadata

data class MetadataValue(
    var value: Any
)
{
    inline fun <reified T> cast() =
        this.value as T
}

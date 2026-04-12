package gg.scala.commons.migrate.annotate

@Migrations("Test")
object test
{
    @Migration(
        id = 1, "GrowlyX", "Move something to new thing"
    )
    fun migrate1()
    {

    }
}

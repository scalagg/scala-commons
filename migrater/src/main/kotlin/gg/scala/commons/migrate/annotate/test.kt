package gg.scala.commons.migrate.annotate

/**
 * @author GrowlyX
 * @since 9/11/2022
 */
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

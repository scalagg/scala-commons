package gg.scala.commons.graduation

/**
 * A migration/versioning system for progressive data objects.
 * Stages are applied in order and tracked via the Progressive.matured set.
 *
 * @author Limo
 * @date 2/5/2026
 */
class Schoolmaster<T : Progressive>
{
    private val stages = mutableListOf<Stage<T>>()

    data class Stage<T>(
        val name: String,
        val block: T.() -> Unit
    )

    fun stage(name: String, block: T.() -> Unit)
    {
        stages.add(Stage(name, block))
    }

    /**
     * Applies any un-matured stages to the progressive object.
     * @return true if any stages were applied (object was modified)
     */
    fun mature(progressive: T): Boolean
    {
        val matured = progressive.matured?.toMutableSet() ?: mutableSetOf()
        var modified = false

        for (stage in stages)
        {
            if (stage.name !in matured)
            {
                stage.block(progressive)
                matured.add(stage.name)
                modified = true
            }
        }

        if (modified)
        {
            progressive.matured = matured
        }

        return modified
    }
}

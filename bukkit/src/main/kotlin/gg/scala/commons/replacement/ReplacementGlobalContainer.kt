package gg.scala.commons.replacement

import gg.scala.commons.annotations.custom.CustomAnnotationProcessors
import gg.scala.commons.replacement.annotation.ReplacementAutoRegister
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan

@Service
@IgnoreAutoScan
object ReplacementGlobalContainer : ReplacementContainer
{
    private val replacers =
        mutableMapOf<String, Replacement>()

    @Configure
    fun configure()
    {
        CustomAnnotationProcessors
            .process<ReplacementAutoRegister> {
                if (it is Replacement)
                {
                    this.register(it)
                }
            }
    }

    fun register(replacement: Replacement)
    {
        if (
            this.replacers
                .containsKey(
                    replacement.id()
                )
        )
        {
            throw IllegalStateException(
                "Replacement with that ID is already registered!"
            )
        }

        this.replacers[replacement.id()] = replacement
    }

    override fun getReplacements() = replacers
}

package gg.scala.commons.replacement.example

import gg.scala.commons.replacement.Replacement
import gg.scala.commons.replacement.ReplacementBuilder
import gg.scala.commons.replacement.annotation.ReplacementAutoRegister

@ReplacementAutoRegister
object ExampleReplacement : Replacement
{
    override fun id() = "player"

    override val replacements =
        ReplacementBuilder
            .newBuilder()

            
            .processStatic("static") {
                "hi wassup!"
            }

            
            .processListed("list") { player, _ ->
                listOf(
                    "Hi ${player.name}",
                    "HOow are U?"
                )
            }

            

            
            .process("horse") { player, params ->
                "${player.name}: $params"
            }
}

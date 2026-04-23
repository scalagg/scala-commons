package gg.scala.commons.replacement.example

import gg.scala.commons.replacement.Replacement
import gg.scala.commons.replacement.ReplacementBuilder
import gg.scala.commons.replacement.annotation.ReplacementAutoRegister

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
@ReplacementAutoRegister
object ExampleReplacement : Replacement
{
    override fun id() = "player"

    override val replacements =
        ReplacementBuilder
            .newBuilder()
            // static processors will not be
            // specific to the player viewing it
            .processStatic("static") {
                "hi wassup!"
            }
            // processes a generic expression and
            // returns a player-specific result list
            .processListed("list") { player, _ ->
                listOf(
                    "Hi ${player.name}",
                    "HOow are U?"
                )
            }
            // similar to processListed(), but only
            // returns a single string.
            //
            // allows multiple groups to be provided
            .process("horse") { player, params ->
                "${player.name}: $params"
            }
}

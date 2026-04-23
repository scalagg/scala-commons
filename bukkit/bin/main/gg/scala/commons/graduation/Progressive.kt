package gg.scala.commons.graduation

/**
 * Interface for objects that can have progressive migrations applied to them.
 * The matured set tracks which migrations have already been applied.
 *
 * @author Limo
 * @date 2/5/2026
 */
interface Progressive
{
    var matured: Set<String>?
}

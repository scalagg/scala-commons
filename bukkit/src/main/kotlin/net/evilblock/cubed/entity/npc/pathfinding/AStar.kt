
package net.evilblock.cubed.entity.npc.pathfinding

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import java.util.*
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sqrt

class AStar
@JvmOverloads constructor(
    private val startLocation: Location,
    private val endLocation: Location,
    maxNodeTests: Int = 1000,
    canClimbLadders: Boolean = false,
    maxFallDistance: Double = 1.0
)
{
    private val startNode: Node
    private var endNode: Node

    private var pathFound = false
    private val checkedNodes = ArrayList<Node>()
    private val uncheckedNodes = ArrayList<Node>()

    private val maxNodeTests: Int
    private val canClimbLadders: Boolean
    private val maxFallDistance: Double

    init
    {
        startNode = Node(startLocation, 0.0, null)
        endNode = Node(endLocation, 0.0, null)

        this.maxNodeTests = maxNodeTests
        this.canClimbLadders = canClimbLadders
        this.maxFallDistance = maxFallDistance
    }

    fun getPath(): MutableList<Location>
    {

        if (!(canStandAt(startLocation) && canStandAt(endLocation)))
        {
            return mutableListOf()
        }

        val nsStart = System.nanoTime()
        uncheckedNodes.add(startNode)

        while (checkedNodes.size < maxNodeTests && !pathFound && uncheckedNodes.size > 0)
        {
            var n = uncheckedNodes[0]
            for (nt in uncheckedNodes)
            {
                if (nt.getEstimatedFinalExpense() < n.getEstimatedFinalExpense())
                {
                    n = nt
                }
            }

            if (n.estimatedExpenseLeft < 1)
            {
                pathFound = true
                endNode = n

                if (debug)
                {
                    printDebug(
                        uncheckedNodes.size.toString() + "uc " + checkedNodes.size + "c " + round(n.expense) + "cne " + round(
                            n.getEstimatedFinalExpense()
                        ) + "cnee "
                    )
                }

                break
            }

            n.getReachableLocations()
            uncheckedNodes.remove(n)
            checkedNodes.add(n)
        }

        if (!pathFound)
        {
            val duration = (System.nanoTime() - nsStart) / 1000000f

            if (debug)
            {
                printDebug(ChatColor.RED.toString() + "A* took " + (if (duration > 50) ChatColor.DARK_RED else ChatColor.RED) + duration + "ms" + ChatColor.RED + " to not find a path.")
            }

            return mutableListOf()
        }

        var length = 1
        var n: Node? = endNode

        while (n!!.origin != null)
        {
            n = n.origin
            length++
        }

        val locations = arrayOfNulls<Location>(length)

        n = endNode
        for (i in length - 1 downTo 1)
        {
            locations[i] = n!!.location
            n = n.origin
        }

        locations[0] = startNode.location

        val duration = (System.nanoTime() - nsStart) / 1000000f

        if (debug)
        {
            printDebug(ChatColor.GREEN.toString() + "A* took " + (if (duration > 50) ChatColor.RED else ChatColor.GREEN) + duration + "ms" + ChatColor.WHITE + " to find a path.")
            printDebug(ChatColor.AQUA.toString() + "A* path size is " + locations.size + ".")
        }

        return locations.filterNotNull().toMutableList()
    }

    private fun getNode(loc: Location): Node
    {
        val test = Node(loc, 0.0, null)

        for (n in checkedNodes)
        {
            if (n.id == test.id) return n
        }

        return test
    }

    inner class Node(val location: Location, var expense: Double, var origin: Node?)
    {
        var id: Double = location.blockX + 30000000.0 * location.blockY + 30000000.0 * 30000000.0 * location.blockZ

        internal var estimatedExpenseLeft = -1.0

        fun getEstimatedFinalExpense(): Double
        {
            if (estimatedExpenseLeft == -1.0) estimatedExpenseLeft = distanceTo(location, endLocation)
            return expense + 1.5 * estimatedExpenseLeft
        }

        fun getReachableLocations()
        {
            for (x in -1..1) for (z in -1..1) if (!(x == 0 && z == 0) && x * z == 0)
            {
                val loc = Location(
                    location.world,
                    floor(location.blockX + x.toDouble()) + 0.5,
                    location.blockY.toDouble(),
                    floor(location.blockZ + z.toDouble()) + 0.5
                )

                if (canStandAt(loc))
                {
                    reachNode(loc, expense + 1)
                }

                if (!isObstructed(loc.clone().add(-x.toDouble(), 2.0, -z.toDouble())))
                {
                    val nLoc = loc.clone().add(0.0, 1.0, 0.0)
                    if (canStandAt(nLoc)) reachNode(nLoc, expense + 1.4142)
                }

                if (!isObstructed(loc.clone().add(0.0, 1.0, 0.0)))
                {
                    val nLoc = loc.clone().add(0.0, -1.0, 0.0)
                    if (canStandAt(nLoc))
                    {
                        reachNode(nLoc, expense + 1.4142)
                    } else if (!isObstructed(nLoc) && !isObstructed(nLoc.clone().add(0.0, 1.0, 0.0)))
                    {
                        var drop = 1
                        while (drop <= maxFallDistance && !isObstructed(loc.clone().add(0.0, -drop.toDouble(), 0.0)))
                        {
                            val locF = loc.clone().add(0.0, -drop.toDouble(), 0.0)
                            if (canStandAt(locF))
                            {
                                val fallNode = addFallNode(loc, expense + 1)
                                fallNode.reachNode(locF, expense + drop * 2)
                            }

                            drop++
                        }
                    }
                }

                if (canClimbLadders) if (loc.clone()
                        .add(-x.toDouble(), 0.0, -z.toDouble()).block.type == Material.LADDER
                )
                {
                    val nLoc = loc.clone().add(-x.toDouble(), 0.0, -z.toDouble())
                    var up = 1
                    while (nLoc.clone().add(0.0, up.toDouble(), 0.0).block.type == Material.LADDER)
                    {
                        up++
                    }

                    reachNode(nLoc.clone().add(0.0, up.toDouble(), 0.0), expense + up * 2)
                }
            }
        }

        fun reachNode(locThere: Location, expenseThere: Double)
        {
            val nt = getNode(locThere)
            if (nt.origin == null && nt !== startNode)
            {
                nt.expense = expenseThere
                nt.origin = this
                uncheckedNodes.add(nt)
                return
            }

            if (nt.expense > expenseThere)
            {
                nt.expense = expenseThere
                nt.origin = this
            }
        }

        fun addFallNode(loc: Location?, expense: Double): Node
        {
            return Node(loc!!, expense, this)
        }
    }

    fun isObstructed(loc: Location): Boolean
    {
        return loc.block.type.isSolid
    }

    fun canStandAt(loc: Location): Boolean
    {
        return !(isObstructed(loc) || isObstructed(loc.clone().add(0.0, 1.0, 0.0)) || !isObstructed(
            loc.clone().add(0.0, -1.0, 0.0)
        ))
    }

    fun distanceTo(loc1: Location, loc2: Location): Double
    {
        if (loc1.world !== loc2.world) return Double.MAX_VALUE
        val deltaX = abs(loc1.x - loc2.x)
        val deltaY = abs(loc1.y - loc2.y)
        val deltaZ = abs(loc1.z - loc2.z)

        val distance2d = sqrt(deltaX * deltaX + deltaZ * deltaZ)
        return sqrt(distance2d * distance2d + deltaY * deltaY)

        
    }

    companion object
    {
        var debug: Boolean = false

        private fun round(d: Double): Double
        {
            return (d * 100).toInt() / 100.0
        }

        private fun printDebug(message: String)
        {
            for (player in Bukkit.getOnlinePlayers())
            {
                if (player.isOp)
                {
                    player.sendMessage(message)
                }
            }
        }
    }

}

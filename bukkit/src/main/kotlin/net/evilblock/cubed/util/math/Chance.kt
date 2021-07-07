/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.util.math

import kotlin.random.Random

object Chance {

    @JvmStatic
    fun percent(chance: Int): Boolean {
        return Random.nextInt(100) >= 100 - chance
    }

    @JvmStatic
    fun percent(chance: Double): Boolean {
        return Random.nextDouble(100.0) >= 100.0 - chance
    }

    @JvmStatic
    fun random(): Boolean {
        return Random.nextBoolean()
    }

    @JvmStatic
    fun pick(range: Int): Int {
        return pick(0, range)
    }

    @JvmStatic
    fun pick(min: Int, max: Int): Int {
        return Random.nextInt(min, max)
    }

    @JvmStatic
    fun <T> weightedPick(list: List<T>, chance: (T) -> Double): T {
        if (list.isEmpty()) {
            throw IllegalStateException("Cannot pick from an empty list")
        }

        if (list.size == 1) {
            return list.first()
        }

        val totalWeight = list.sumByDouble { chance.invoke(it) }

        var randomIndex = -1
        var random = Math.random() * totalWeight

        for ((index, entry) in list.withIndex()) {
            random -= chance.invoke(entry)
            if (random <= 0.0) {
                randomIndex = index
                break
            }
        }

        return list[randomIndex]
    }

}
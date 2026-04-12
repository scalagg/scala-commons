package gg.scala.commons.cooldown

import io.lettuce.core.api.StatefulRedisConnection
import me.lucko.helper.cooldown.CooldownMap

interface DistributedCooldownMap<T> : CooldownMap<T>
{
    val connection: StatefulRedisConnection<String, String>

    fun serializeIntoKey(t: T): String
}

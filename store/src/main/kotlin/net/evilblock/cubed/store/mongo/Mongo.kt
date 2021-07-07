/*
 * Copyright (c) 2020. Joel Evans
 *
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Joel Evans
 */

package net.evilblock.cubed.store.mongo

import com.mongodb.*
import java.io.Closeable

class Mongo : Closeable {

    lateinit var client: MongoClient

    fun connect(uri: String) {
        client = MongoClient(MongoClientURI(uri))
    }

    override fun close() {
        client.close()
    }

}

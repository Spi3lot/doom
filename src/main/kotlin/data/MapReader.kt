package org.spi3lot.data

import kotlin.random.Random

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
object MapReader {

    fun readMap(path: String): DoomMap {
        return Array(10) { Array(10) { if (Random.nextBoolean()) randomWallColor() else null } }
    }

    private fun randomWallColor(): Int {
        return Random.nextInt() and 0xFFFF77 or 0xFF000000.toInt()
    }

}
package org.spi3lot.data

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
object MapReader {

    private val random = java.util.Random()

    fun readMap(path: String): DoomMap {
        return Array(10) { Array(10) { if (random.nextBoolean()) randomWallColor()else null } }
    }

    private fun randomWallColor(): Int {
        return random.nextInt() and 0xFFFF00 or 0xFF000000.toInt()
    }

}
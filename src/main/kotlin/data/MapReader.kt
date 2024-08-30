package org.spi3lot.data

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
object MapReader {

    fun readMap(path: String): DoomMap {
        return Array(10) { Array(10) { if (Math.random() < 0.5) 0xFF00FF00.toInt() else null } }
    }

}
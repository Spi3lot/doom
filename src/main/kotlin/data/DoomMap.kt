package org.spi3lot.data

import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
typealias DoomMap = Array<Array<Int?>>

fun DoomMap.containsPosition(position: PVector): Boolean {
    return position.x in 0f..<size.toFloat() && position.y in 0f..<this[0].size.toFloat()
}
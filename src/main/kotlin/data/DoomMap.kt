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

fun DoomMap.screenToWorld(screenPosition: PVector, settings: Settings): PVector {
    return PVector(this[0].size * screenPosition.x / settings.width, size * screenPosition.y / settings.height)
}

fun DoomMap.worldToScreen(worldPosition: PVector, settings: Settings): PVector {
    return PVector(settings.width * worldPosition.x / this[0].size, settings.height * worldPosition.y / size)
}

fun DoomMap.getTileColor(position: PVector): Int? {
    return this[position.y.toInt()][position.x.toInt()]
}
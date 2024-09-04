package org.spi3lot.data

import org.spi3lot.math.PVectorOperators.div
import org.spi3lot.math.PVectorOperators.times
import processing.core.PApplet.floor
import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
typealias DoomMap = Array<Array<Int?>>

val DoomMap.sizeVector: PVector
    get() = PVector(this[0].size.toFloat(), size.toFloat())

fun DoomMap.containsPosition(position: PVector): Boolean {
    return position.x in 0f..<size.toFloat() && position.y in 0f..<this[0].size.toFloat()
}

fun DoomMap.screenToWorld(screenPosition: PVector, settings: Settings): PVector {
    return screenPosition / calcRectDimensions(settings)
}

fun DoomMap.worldToScreen(worldPosition: PVector, settings: Settings): PVector {
    return worldPosition * calcRectDimensions(settings)
}

fun DoomMap.calcRectDimensions(settings: Settings): PVector {
    return settings.dimensions / sizeVector
}

fun DoomMap.getTileColor(position: PVector): Int? {
    return getOrNull(floor(position.y))?.getOrNull(floor(position.x))
}
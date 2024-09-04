package org.spi3lot.data

import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
data class Settings(
    val width: Int,
    val height: Int,
    val worldScale: Float,
    val speedMultiplier: Float,
    val fov: Float,
) {

    val dimensions = PVector(width.toFloat(), height.toFloat())

    val playerSpeed = speedMultiplier / worldScale

    fun distance(position1: PVector, position2: PVector): Float {
        return worldScale * PVector.dist(position1, position2)
    }

}
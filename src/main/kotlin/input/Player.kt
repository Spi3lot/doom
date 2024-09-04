package org.spi3lot.input

import org.spi3lot.data.DoomMap
import org.spi3lot.math.PVectorOperators.times
import processing.core.PConstants.HALF_PI
import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
class Player(
    val position: PVector = PVector(),
    var direction: Float = 0f,
) {

    fun getLeftMostRayDirection(fov: Float): PVector {
        return PVector.fromAngle(direction - fov / 2)
    }

    fun getRightMostRayDirection(fov: Float): PVector {
        return PVector.fromAngle(direction + fov / 2)
    }

    fun moveBackward(map: DoomMap, speed: Float = 1f) {
        moveForward(map, -speed)
    }

    fun moveLeft(map: DoomMap, speed: Float = 1f) {
        moveRight(map, -speed)
    }

    fun moveForward(map: DoomMap, speed: Float = 1f) {
        position.add(PVector.fromAngle(direction) * speed)
    }

    fun moveRight(map: DoomMap, speed: Float = 1f) {
        position.add(PVector.fromAngle(direction + HALF_PI) * speed)
    }

}
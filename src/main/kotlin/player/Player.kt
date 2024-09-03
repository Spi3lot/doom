package org.spi3lot.player

import org.spi3lot.data.DoomMap
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

    fun moveBackward(map: DoomMap, speed: Float = 1f) {
        moveForward(map, -speed)
    }

    fun moveLeft(map: DoomMap, speed: Float = 1f) {
        moveRight(map, -speed)
    }

    fun moveForward(map: DoomMap, speed: Float = 1f) {
        position.add(PVector.fromAngle(direction).mult(speed))
    }

    fun moveRight(map: DoomMap, speed: Float = 1f) {
        position.add(PVector.fromAngle(direction + HALF_PI).mult(speed))
    }

}
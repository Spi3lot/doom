package org.spi3lot.rendering

import processing.core.PApplet.ceil
import processing.core.PApplet.floor
import processing.core.PApplet.min
import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
class Ray(
    val position: PVector = PVector(),
    val direction: PVector = PVector(),
    private val maxSteps: Int = 25,
    private val epsilon: Float = 0.001f,
) {

    private var stepCount = 0

    fun canStep(): Boolean {
        return stepCount < maxSteps
    }

    fun step() {
        val targetX = if (direction.x > 0) floor(position.x + 1) else ceil(position.x - 1)
        val targetY = if (direction.y > 0) floor(position.y + 1) else ceil(position.y - 1)
        val t = min((targetX - position.x) / direction.x, (targetY - position.y) / direction.y)
        position.add(direction.mult(t + epsilon))
        stepCount++
    }

    fun reset() {
        stepCount = 0
    }

}
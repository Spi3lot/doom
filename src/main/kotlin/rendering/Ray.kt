package org.spi3lot.rendering

import org.spi3lot.data.DoomMap
import org.spi3lot.data.getTileColor
import processing.core.PApplet
import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
class Ray(
    val position: PVector = PVector(),
    val direction: PVector = PVector(),
) {

    fun findIntersection(map: DoomMap, iterations: Int = 10) {
        var direction = -1

        for (i in 1..iterations) {
            step(direction * PApplet.pow(2f, -i.toFloat()))
            val color = map.getTileColor(position)
            direction = if (color != null) -1 else 1
        }
    }

    fun step(): Pair<Int, Int> {
        val oldPosition = position.copy()
        position.add(direction)
        return (position.x.toInt() - oldPosition.x.toInt()) to (position.y.toInt() - oldPosition.y.toInt())
    }

    fun step(multiplier: Float) {
        position.add(PVector.mult(direction, multiplier))
    }

}
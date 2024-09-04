package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
import org.spi3lot.data.containsPosition
import org.spi3lot.data.getTileColor
import org.spi3lot.data.worldToScreen
import org.spi3lot.player.Player
import processing.core.PVector
import kotlin.collections.indices
import kotlin.math.abs
import kotlin.math.cos

/**
 *  @since 03.09.2024, Di.
 *  @author Emilio Zottel
 */
object Draw {

    fun Doom.drawMap(
        map: DoomMap,
        drawRays: Boolean = false,
    ) {
        val rectWidth = width / map[0].size
        val rectHeight = height / map.size

        for (j in map.indices) {
            val row = map[j]

            for (i in row.indices) {
                fill(row[i] ?: continue)
                stroke(0)
                rect(i * rectWidth.toFloat(), j * rectHeight.toFloat(), rectWidth.toFloat(), rectHeight.toFloat())
            }
        }

        val screenPosition = map.worldToScreen(player.position, settings)
        stroke(0)
        strokeWeight(5f)
        fill(255f, 0f, 0f)
        circle(screenPosition.x, screenPosition.y, 10f)

        val offset = PVector.fromAngle(settings.fov / 2).mult(10f)
        pushMatrix()
        translate(screenPosition.x, screenPosition.y)
        rotate(player.direction)
        stroke(0)
        strokeWeight(5f)
        line(0f, 0f, offset.x, offset.y)
        line(0f, 0f, offset.x, -offset.y)
        popMatrix()

        if (drawRays) {
            render(player, map, true)
        }
    }

    fun Doom.render(
        player: Player,
        map: DoomMap,
        drawRays: Boolean = false,
    ) {
        val ray = Ray()

        for (x in 0..<width) {
            ray.position.set(player.position)
            ray.direction.set(PVector.fromAngle(player.direction + calcAngleForColumn(x)))
            castRay(ray, player, map, drawRays, x)
        }
    }

    private fun Doom.castRay(
        ray: Ray,
        player: Player,
        map: DoomMap,
        drawRays: Boolean,
        x: Int,
    ) {
        var collidedDiagonally = false

        while (map.containsPosition(ray.position)) {
            val color = map.getTileColor(ray.position)

            if (drawRays) {
                drawRayPosition(ray, map)
            }

            if (color != null) {
                ray.findIntersection(map)

                if (drawRays) {
                    drawRayIntersection(ray, map)
                } else {
                    val distance = PVector.dist(player.position, ray.position)
                    drawWallLine(distance, color, x)
                }

                return
            }

            val (dx, dy) = ray.step()

            if (dx != 0 && dy != 0) {
                check (abs(dx) <= 1 || abs(dy) <= 1) { "dx and dy must be 0 or 1" }
            }
        }
    }

    private fun Doom.drawRayPosition(ray: Ray, map: DoomMap) {
        val screenPosition = map.worldToScreen(ray.position, settings)
        stroke(0f, 0f, 255f)
        point(screenPosition.x, screenPosition.y)
    }

    private fun Doom.drawRayIntersection(ray: Ray, map: DoomMap) {
        val intersectionScreenPosition = map.worldToScreen(ray.position, settings)
        stroke(255f, 0f, 0f)
        strokeWeight(5f)
        point(intersectionScreenPosition.x, intersectionScreenPosition.y)
    }

    private fun Doom.drawWallLine(distance: Float, color: Int, x: Int) {
        val wallHeight = height / (1 + distance * cos(calcAngleForColumn(x)))
        stroke(color)
        strokeWeight(1f)
        line(x.toFloat(), (height - wallHeight) / 2, x.toFloat(), (height + wallHeight) / 2)
    }

    private fun Doom.calcAngleForColumn(x: Int): Float {
        return settings.fov * (x / width.toFloat() - 0.5f)
    }

}
package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
import org.spi3lot.data.calcRectDimensions
import org.spi3lot.data.getTileColor
import org.spi3lot.data.worldToScreen
import org.spi3lot.math.PVectorOperators.times
import processing.core.PVector
import kotlin.collections.indices
import kotlin.math.cos

/**
 *  @since 03.09.2024, Di.
 *  @author Emilio Zottel
 */
object Draw {

    fun Doom.drawMap(map: DoomMap, drawRays: Boolean = false) {
        val rectDimensions = map.calcRectDimensions(settings)

        for (j in map.indices) {
            val row = map[j]

            for (i in row.indices) {
                fill(row[i] ?: continue)
                stroke(0)
                rect(i * rectDimensions.x, j * rectDimensions.y, rectDimensions.x, rectDimensions.y)
            }
        }

        val screenPosition = map.worldToScreen(player.position, settings)
        stroke(0)
        strokeWeight(5f)
        fill(255f, 0f, 0f)
        circle(screenPosition.x, screenPosition.y, 10f)

        val offset = PVector.fromAngle(settings.fov / 2) * 50f
        pushMatrix()
        translate(screenPosition.x, screenPosition.y)
        rotate(player.direction)
        stroke(0)
        strokeWeight(5f)
        line(0f, 0f, offset.x, offset.y)
        line(0f, 0f, offset.x, -offset.y)
        popMatrix()

        if (drawRays) {
            render(map, true)
        }
    }

    fun Doom.render(map: DoomMap, drawRays: Boolean = false) {
        val ray = Ray()
        val leftMostRay = player.getLeftMostRayDirection(settings.fov)
        val rightMostRay = player.getRightMostRayDirection(settings.fov)

        for (x in 0..<width) {
            ray.position.set(player.position)
            ray.direction.set(PVector.lerp(leftMostRay, rightMostRay, x / width.toFloat()))
            castRay(ray, map, drawRays, x)
            ray.reset()
        }
    }

    private fun Doom.castRay(ray: Ray, map: DoomMap, drawRays: Boolean, x: Int) {
        while (ray.canStep()) {
            val color = map.getTileColor(ray.position)

            if (color != null) {
                if (drawRays) {
                    drawRayIntersection(ray, map)
                } else {
                    drawWallLine(ray, color, x)
                }

                return
            }

            if (drawRays) {
                drawRayPosition(ray, map)
            }

            ray.step()
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

    private fun Doom.drawWallLine(ray: Ray, color: Int, x: Int) {
        val distance = settings.distance(ray.position, player.position)
        val adjustedDistance = distance * cos(ray.direction.heading() - player.direction)
        val wallHeight = height / adjustedDistance
        stroke(color)
        strokeWeight(1f)
        line(x.toFloat(), (height - wallHeight) / 2, x.toFloat(), (height + wallHeight) / 2)
    }

}
package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
import org.spi3lot.data.getTileColor
import org.spi3lot.data.worldToScreen
import org.spi3lot.rendering.Draw.drawWallLine
import processing.core.PVector
import kotlin.math.cos
import kotlin.math.max

/**
 *  @since 12.09.2024, Do.
 *  @author Emilio Zottel
 */
object RaycastCpu {

    internal fun Doom.renderCpu(
        map: DoomMap,
        drawRays: Boolean,
        leftMostRay: PVector,
        rightMostRay: PVector,
    ) {
        val ray = Ray()

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
                    val distance = settings.distance(ray.position, player.position)
                    val adjustedDistance = distance * cos(ray.direction.heading() - player.heading)
                    val wallHeight = height / max(1f, adjustedDistance)
                    drawWallLine(x, wallHeight, color)
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

}
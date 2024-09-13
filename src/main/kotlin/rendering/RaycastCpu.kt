package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
import org.spi3lot.data.getTileColor
import org.spi3lot.data.worldToScreen
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
        leftMostRay: PVector,
        rightMostRay: PVector,
        drawRays: Boolean,
    ) {
        val ray = Ray()

        if (drawRays) {
            for (x in 0..<width) {
                ray.position.set(player.position)
                ray.direction.set(PVector.lerp(leftMostRay, rightMostRay, x / width.toFloat()))
                castRay(ray, map)
                ray.reset()
            }
        } else {
            val wallHeights = IntArray(width)
            val colors = IntArray(width)

            for (x in 0..<width) {
                ray.position.set(player.position)
                ray.direction.set(PVector.lerp(leftMostRay, rightMostRay, x / width.toFloat()))
                castRay(ray, map, wallHeights, colors, x)
                ray.reset()
            }

            lineShader.set("wallHeights", wallHeights)
            lineShader.set("colors", colors)
        }
    }

    private fun Doom.castRay(
        ray: Ray,
        map: DoomMap,
        wallHeights: IntArray,
        colors: IntArray,
        x: Int,
    ) {
        while (ray.canStep()) {
            val color = map.getTileColor(ray.position)

            if (color != null) {
                val distance = settings.distance(ray.position, player.position)
                val adjustedDistance = distance * cos(ray.direction.heading() - player.heading)
                val wallHeight = height / max(1f, adjustedDistance)
                wallHeights[x] = wallHeight.toInt()
                colors[x] = color
                return
            }

            ray.step()
        }
    }

    private fun Doom.castRay(ray: Ray, map: DoomMap) {
        while (ray.canStep()) {
            val color = map.getTileColor(ray.position)

            if (color != null) {
                drawRayIntersection(ray, map)
                return
            }

            drawRayPosition(ray, map)
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
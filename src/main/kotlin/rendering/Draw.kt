package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
import org.spi3lot.data.calcRectDimensions
import org.spi3lot.data.worldToScreen
import org.spi3lot.math.PVectorOperators.times
import org.spi3lot.rendering.RaycastCpu.renderCpu
import org.spi3lot.rendering.RaycastGpu.renderGpu
import processing.core.PVector
import kotlin.collections.indices

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
        rotate(player.heading)
        stroke(0)
        strokeWeight(5f)
        line(0f, 0f, offset.x, offset.y)
        line(0f, 0f, offset.x, -offset.y)
        popMatrix()

        if (drawRays) {
            render(map, gpu = false, drawRays = true)
        }
    }

    fun Doom.render(map: DoomMap, gpu: Boolean = true, drawRays: Boolean = false) {
        val leftMostRay = player.getLeftMostRayDirection(settings.fov)
        val rightMostRay = player.getRightMostRayDirection(settings.fov)

        if (gpu) {
            renderGpu(map, leftMostRay, rightMostRay)
        } else {
            renderCpu(map, leftMostRay, rightMostRay, drawRays)
        }
    }

}
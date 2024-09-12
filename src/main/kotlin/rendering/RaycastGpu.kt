package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
import org.spi3lot.rendering.Draw.drawWallLine
import processing.core.PVector

/**
 *  @since 12.09.2024, Do.
 *  @author Emilio Zottel
 */
object RaycastGpu {

    init {
        System.loadLibrary("lib/raycast")
    }

    internal fun Doom.renderGpu(map: DoomMap, leftMostRay: PVector, rightMostRay: PVector) {
        val wallHeights = FloatArray(width)
        val colors = IntArray(width)

        castCudaRays(
            map,
            width,
            Ray.maxSteps,
            Ray.epsilon,
            player.position.x,
            player.position.y,
            player.heading,
            leftMostRay.x,
            leftMostRay.y,
            rightMostRay.x,
            rightMostRay.y,
            wallHeights,
            colors
        )

        for (x in 0..<width) {
            drawWallLine(x, wallHeights[x], colors[x])
        }
    }

    private external fun castCudaRays(
        map: DoomMap,
        width: Int,
        maxSteps: Int,
        epsilon: Float,
        playerX: Float,
        playerY: Float,
        playerHeading: Float,
        leftMostRayDirectionX: Float,
        leftMostRayDirectionY: Float,
        rightMostRayDirectionX: Float,
        rightMostRayDirectionY: Float,
        wallHeights: FloatArray,
        colors: IntArray,
    )

}
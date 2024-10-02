package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
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
        val windowWidth = width
        val windowHeight = height
        val wallHeights = IntArray(windowWidth)
        val colors = IntArray(windowWidth)

        castCudaRays(
            map,
            settings.worldScale,
            windowWidth,
            windowHeight,
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

        dataTexture.loadPixels()

        for (x in 0..<windowWidth) {
            dataTexture.pixels[x] = wallHeights[x]
            dataTexture.pixels[windowWidth + x] = colors[x]
        }

        dataTexture.updatePixels()
    }

    private external fun castCudaRays(
        map: DoomMap,
        worldScale: Float,
        windowWidth: Int,
        windowHeight: Int,
        maxSteps: Int,
        epsilon: Float,
        playerX: Float,
        playerY: Float,
        playerHeading: Float,
        leftMostRayDirectionX: Float,
        leftMostRayDirectionY: Float,
        rightMostRayDirectionX: Float,
        rightMostRayDirectionY: Float,
        wallHeights: IntArray,
        colors: IntArray,
    )

}
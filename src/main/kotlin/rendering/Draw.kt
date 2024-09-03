package org.spi3lot.rendering

import org.spi3lot.Doom
import org.spi3lot.data.DoomMap
import org.spi3lot.data.containsPosition
import org.spi3lot.player.Player
import processing.core.PVector
import kotlin.collections.indices

/**
 *  @since 03.09.2024, Di.
 *  @author Emilio Zottel
 */
object Draw {

    fun Doom.drawMap(map: DoomMap) {
        val w = width / map[0].size
        val h = height / map.size

        for (j in map.indices) {
            val row = map[j]

            for (i in row.indices) {
                fill(row[i] ?: continue)
                rect(i * w.toFloat(), j * h.toFloat(), w.toFloat(), h.toFloat())
            }
        }

        val screenPosition = PVector(width * player.position.x / w, height * player.position.y / h)
        fill(255f, 0f, 0f)
        circle(screenPosition.x, screenPosition.y, 10f)

        val offset = PVector.fromAngle(settings.fov / 2).mult(10f)
        translate(screenPosition.x, screenPosition.y)
        rotate(player.direction)
        stroke(0)
        line(0f, 0f, offset.x, offset.y)
        line(0f, 0f, offset.x, -offset.y)
    }

    fun Doom.drawRender(player: Player, map: DoomMap) {
        val ray = Ray()

        for (i in 0..<width) {
            ray.position.set(player.position)
            ray.direction.set(PVector.fromAngle(player.direction + settings.fov * (i / width.toFloat() - 0.5f)))

            while (map.containsPosition(ray.position)) {
                val color = map[ray.position.y.toInt()][ray.position.x.toInt()]

                if (color != null) {
                    val distance = PVector.dist(player.position, ray.position)
                    val wallHeight = height / distance
                    stroke(color)
                    line(i.toFloat(), (height - wallHeight) / 2, i.toFloat(), (height + wallHeight) / 2)
                    break
                }

                ray.step()
            }
        }
    }

}
package org.spi3lot

import org.spi3lot.data.DoomMap
import org.spi3lot.data.MapReader
import org.spi3lot.data.Ray
import org.spi3lot.data.Settings
import org.spi3lot.data.containsPosition
import org.spi3lot.player.Player
import processing.core.PApplet
import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
fun main() {
    PApplet.main(Doom::class.java)
}

class Doom : PApplet() {

    private val player = Player(PVector(5f, 5f))

    private val settings = Settings(800, 600, 90f)

    private val backgroundColor = color(0, 255, 255)

    private var map = MapReader.readMap("")

    private var showMap = false

    override fun settings() {
        size(settings.width, settings.height)
    }

    override fun draw() {
        background(backgroundColor)
        val drawFunction = if (showMap) ::drawMap else ::drawRender
        drawFunction(map)
    }

    private fun drawMap(map: DoomMap) {
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

        val offset = PVector.fromAngle(settings.fov / 2).mult(50f)  // TODO: make work
        rotate(player.direction)
        stroke(0)
        line(screenPosition.x, screenPosition.y, screenPosition.x + offset.x, screenPosition.y + offset.y)
        line(screenPosition.x, screenPosition.y, screenPosition.x + offset.x, screenPosition.y - offset.y)
    }

    private fun drawRender(map: DoomMap) {
        val ray = Ray()

        for (i in 0..<width) {
            ray.position.set(player.position)
            ray.direction.set(PVector.fromAngle(player.direction + settings.fov * (i / width.toFloat() - 0.5f)))

            while (map.containsPosition(ray.position)) {
                val color = map[ray.position.x.toInt()][ray.position.y.toInt()]

                if (color != null) {
                    stroke(color)
                    line(i.toFloat(), 0f, i.toFloat(), height.toFloat())
                    break
                }

                ray.step()
            }
        }
    }

    override fun keyPressed() {
        when (key.uppercaseChar()) {
            'M' -> showMap = !showMap
            'R' -> map = MapReader.readMap("")
        }
    }

}
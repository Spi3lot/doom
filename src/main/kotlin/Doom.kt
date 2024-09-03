package org.spi3lot

import org.spi3lot.data.MapReader
import org.spi3lot.data.Settings
import org.spi3lot.player.Player
import org.spi3lot.rendering.Draw.drawMap
import org.spi3lot.rendering.Draw.drawRender
import processing.core.PApplet
import processing.core.PVector
import processing.event.KeyEvent
import processing.event.MouseEvent

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
fun main() {
    PApplet.main(Doom::class.java)
}

class Doom : PApplet() {

    val player = Player(PVector(5f, 5f))

    val settings = Settings(800, 600, HALF_PI)

    private val backgroundColor = color(0, 255, 255)

    private var map = MapReader.readMap("")

    private var showMap = false

    override fun settings() {
        size(settings.width, settings.height)
    }

    override fun setup() {
        noCursor()
    }

    override fun draw() {
        background(backgroundColor)

        if (showMap) {
            drawMap(map)
        } else {
            drawRender(player, map)
        }
    }

    override fun keyPressed(event: KeyEvent) {
        when (event.key.uppercaseChar()) {
            'R' -> map = MapReader.readMap("")
            'M' -> showMap = !showMap
            'W' -> player.moveForward(map, 0.1f)
            'S' -> player.moveBackward(map, 0.1f)
            'A' -> player.moveLeft(map, 0.1f)
            'D' -> player.moveRight(map, 0.1f)
        }
    }

    override fun mouseDragged(event: MouseEvent) {
        player.direction += (mouseX - pmouseX) * 0.01f
    }

}
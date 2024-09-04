package org.spi3lot

import org.spi3lot.data.MapReader
import org.spi3lot.data.Settings
import org.spi3lot.input.KeyHandler
import org.spi3lot.input.Player
import org.spi3lot.rendering.Draw.drawMap
import org.spi3lot.rendering.Draw.render
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

    val settings = Settings(
        width = 600,
        height = 600,
        worldScale = 3f,
        speedMultiplier = 5f,
        fov = PI * 0.6f
    )

    private val deltaTime: Float
        get() = 1 / frameRate

    private val keyHandler = KeyHandler()

    private val backgroundColor = color(0, 255, 255)

    private var map = MapReader.readMap("")

    private var showMap = false

    override fun settings() {
        size(settings.width, settings.height)
    }

    override fun setup() {
        keyHandler.addKeyAction('W') { player.moveForward(map, settings.playerSpeed * deltaTime) }
        keyHandler.addKeyAction('A') { player.moveLeft(map, settings.playerSpeed * deltaTime) }
        keyHandler.addKeyAction('S') { player.moveBackward(map, settings.playerSpeed * deltaTime) }
        keyHandler.addKeyAction('D') { player.moveRight(map, settings.playerSpeed * deltaTime) }
        noCursor()
    }

    override fun draw() {
        keyHandler.invokeActions()
        background(backgroundColor)

        if (showMap) {
            drawMap(map, drawRays = true)
        } else {
            render(map)
        }
    }

    override fun keyPressed(event: KeyEvent) {
        keyHandler.keyPressed(event.keyCode)

        when (event.keyCode) {
            'R'.code -> map = MapReader.readMap("")
            'M'.code -> showMap = !showMap
        }
    }

    override fun keyReleased(event: KeyEvent) {
        keyHandler.keyReleased(event.keyCode)
    }

    override fun mouseDragged(event: MouseEvent) {
        player.direction += (mouseX - pmouseX) * 0.005f
    }

}
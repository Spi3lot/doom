package org.spi3lot

import org.spi3lot.data.MapReader
import org.spi3lot.data.Settings
import org.spi3lot.input.KeyHandler
import org.spi3lot.player.Player
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

    val settings = Settings(800, 800, PI * 0.6f)

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
        keyHandler.addKeyAction('W') { player.moveForward(map, deltaTime * 2) }
        keyHandler.addKeyAction('A') { player.moveLeft(map, deltaTime * 2) }
        keyHandler.addKeyAction('S') { player.moveBackward(map, deltaTime * 2) }
        keyHandler.addKeyAction('D') { player.moveRight(map, deltaTime * 2) }
        noCursor()
    }

    override fun draw() {
        keyHandler.invokeActions()
        background(backgroundColor)

        if (showMap) {
            drawMap(map, drawRays = true)
        } else {
            render(map, player)
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
        player.direction += (mouseX - pmouseX) * 0.01f
    }

}
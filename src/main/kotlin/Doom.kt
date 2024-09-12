package org.spi3lot

import org.spi3lot.data.MapReader
import org.spi3lot.data.Settings
import org.spi3lot.input.KeyHandler
import org.spi3lot.input.Player
import org.spi3lot.rendering.Draw.drawMap
import org.spi3lot.rendering.Draw.render
import org.spi3lot.time.Time
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

    private val keyHandler = KeyHandler()

    private val backgroundColor = color(0, 255, 255)

    private var map = MapReader.readMap("")

    private var showMap = false

    override fun settings() {
        size(settings.width, settings.height)
    }

    override fun setup() {
        frameRate(1000f)
        windowResizable(true)
        keyHandler.addKeyAction('W') { player.moveForward(map, settings.playerSpeed * Time.deltaTime); }
        keyHandler.addKeyAction('A') { player.moveLeft(map, settings.playerSpeed * Time.deltaTime) }
        keyHandler.addKeyAction('S') { player.moveBackward(map, settings.playerSpeed * Time.deltaTime) }
        keyHandler.addKeyAction('D') { player.moveRight(map, settings.playerSpeed * Time.deltaTime) }
    }

    override fun draw() {
        Time.updateDeltaTime()
        keyHandler.invokeActions()
        background(backgroundColor)

        if (showMap) {
            drawMap(map, drawRays = true)
        } else {
            render(map, settings.gpu)
        }

        fill(255)
        textSize(20f)
        text("FPS: ${(1 / Time.deltaTime).toInt()}", 10f, 20f)
    }

    override fun keyPressed(event: KeyEvent) {
        keyHandler.keyPressed(event.keyCode)

        when (event.keyCode) {
            'R'.code -> map = MapReader.readMap("")
            'M'.code -> showMap = !showMap
            'C'.code -> settings.gpu = false
            'G'.code -> settings.gpu = true
        }
    }

    override fun keyReleased(event: KeyEvent) {
        keyHandler.keyReleased(event.keyCode)
    }

    override fun mouseDragged(event: MouseEvent) {
        player.heading += (mouseX - pmouseX) * 0.005f
    }

    override fun windowResized() {
        settings.width = width
        settings.height = height
    }

}
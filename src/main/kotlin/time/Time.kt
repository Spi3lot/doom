package org.spi3lot.time

/**
 *  @since 12.09.2024, Do.
 *  @author Emilio Zottel
 */
object Time {

    var deltaTime = 0f
        private set

    private var lastNanos = System.nanoTime()

    fun updateDeltaTime() {
        val nanos = System.nanoTime()
        deltaTime = (nanos - lastNanos) / 1e9f
        lastNanos = nanos
    }

}
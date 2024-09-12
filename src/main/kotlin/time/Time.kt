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
        deltaTime = (System.nanoTime() - lastNanos) / 1e9f
    }

}
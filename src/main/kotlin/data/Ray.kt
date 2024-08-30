package org.spi3lot.data

import processing.core.PVector

/**
 *  @since 30.08.2024, Fr.
 *  @author Emilio Zottel
 */
data class Ray(
    val position: PVector = PVector(),
    val direction: PVector = PVector(),
) {

    fun step() {
        position.add(direction)
    }

}
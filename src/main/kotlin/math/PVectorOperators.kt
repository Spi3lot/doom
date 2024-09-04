package org.spi3lot.math

import processing.core.PVector

/**
 *  @since 04.09.2024, Mi.
 *  @author Emilio Zottel
 */
object PVectorOperators {

    operator fun PVector.plus(v: PVector): PVector {
        return PVector.add(this, v)
    }

    operator fun PVector.minus(v: PVector): PVector {
        return PVector.sub(this, v)
    }

    operator fun PVector.times(n: Float): PVector {
        return PVector.mult(this, n)
    }

    operator fun PVector.times(v: PVector): PVector {
        return PVector(x * v.x, y * v.y, z * v.z)
    }

    operator fun PVector.div(scalar: Float): PVector {
        return PVector.div(this, scalar)
    }

    operator fun PVector.div(v: PVector): PVector {
        return PVector(x / v.x, y / v.y, z / v.z)
    }

}
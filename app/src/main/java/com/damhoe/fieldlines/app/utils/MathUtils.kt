package com.damhoe.fieldlines.app.utils

import kotlin.math.ceil

object MathUtils {
    /**
     * Rounds up any floating point number to the nearest multiple
     * of the increment.
     */
    fun roundUpToNearest(value: Double, increment: Double): Double =
        if (increment == 0.0) {
            value
        } else {
            ceil(value / increment) * increment
        }
}
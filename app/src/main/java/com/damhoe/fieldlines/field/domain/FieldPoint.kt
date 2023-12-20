package com.damhoe.fieldlines.field.domain

import kotlin.math.sqrt

/**
 *  The field point characterizes the electric field at a certain position
 *  by storing the field strength as a vector.
 *
 * Created by damian on 25.11.2017.
 */
data class FieldPoint(
    val x: Double,
    val y: Double,
    val forceX: Double,
    val forceY: Double
) {
    private val f = sqrt(forceX * forceX + forceY * forceY)
    val relForceX = forceX / f
    val relForceY = forceY / f
}
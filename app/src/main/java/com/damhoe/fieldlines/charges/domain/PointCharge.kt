package com.damhoe.fieldlines.charges.domain

import android.graphics.PointF
import java.util.UUID

/**
 * A point charge is fully characterized by its position (x, y) and its charge.
 *
 * Created on 25.11.2017.
 */
data class PointCharge(
    var id: Long = UUID.randomUUID().mostSignificantBits,
    var position: PointF,
    var charge: Double
) {
    fun copy(): PointCharge = PointCharge(id, position, charge)
}
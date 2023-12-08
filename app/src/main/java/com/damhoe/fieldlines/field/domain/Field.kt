package com.damhoe.fieldlines.field.domain

import android.graphics.PointF
import com.damhoe.fieldlines.app.exception.PointsTooCloseException
import com.damhoe.fieldlines.charges.domain.PointCharge
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.math.sqrt

/**
 * Fields hold the configuration of the charges.
 *
 * Created at 4.12.2023
 */
class Field {
    private val nearestDistanceOfCharges = 0.2
    private val mutablePointCharges: MutableList<PointCharge> = mutableListOf()


    val pointCharges: List<PointCharge>
        get() = mutablePointCharges.toList()

    fun addPointCharge(pointCharge: PointCharge): Result<Unit> {
        if (isPositionOccupied(pointCharge.position)) {
            return failure(PointsTooCloseException("At this point, a charge already exists"))
        }
        mutablePointCharges.add(pointCharge)
        return success(Unit)
    }

    fun addPointChargeAt(position: Int, pointCharge: PointCharge): Result<Unit> {
        if (isPositionOccupied(pointCharge.position)) {
            return failure(PointsTooCloseException("At this point, a charge already exists"))
        }
        mutablePointCharges.add(position, pointCharge)
        return success(Unit)
    }

    private fun isPositionOccupied(point: PointF, exceptId: Long = -1) =
        mutablePointCharges.filterNot { it.id == exceptId }.any { isNear(it.position, point) }

    private fun isNear(p1: PointF, p2: PointF): Boolean {
        val dx = p1.x - p2.x
        val dy = p1.y - p2.y
        return (dx * dx + dy * dy) < nearestDistanceOfCharges
    }

    fun removePointChargeAt(position: Int): Result<PointCharge> =
        mutablePointCharges.getOrNull(position)?.run {
            success(mutablePointCharges.removeAt(position))
        } ?: failure(IndexOutOfBoundsException(
            "Position $position exceeds number of point charges")
        )

    fun updatePointChargeAt(
        position: Int,
        newX: Float,
        newY: Float,
        newCharge: Double
    ): Result<Unit> = mutablePointCharges.getOrNull(position)?.run {
        if (isPositionOccupied(PointF(newX, newY), id)) {
            return failure(PointsTooCloseException("At this point, a charge already exists"))
        }
        this.position = PointF(newX, newY)
        charge = newCharge
        success(Unit)
    } ?: failure(IndexOutOfBoundsException(
        "Position $position exceeds number of point charges")
    )
    fun moveAlongFieldLine(fieldPoint: FieldPoint, ratio: Float, direction: Int): PointF =
        fieldPoint.run {
            val scale = 10.0 * ratio * direction
            PointF((x + scale * relForceX).toFloat(), (y + scale * relForceY).toFloat())
        }

    // The equivalent of f = 1 / 4 * Pi * e0
    // in reasonable units for the scaling of the field lines
    private val f = 100.0

    @Suppress("LocalVariableName")
    fun calculateFieldPoint(point: PointF): FieldPoint {
        var Ex = 0.0
        var Ey = 0.0

        for (charge in pointCharges) {
            val dx = point.x - charge.position.x
            val dy = point.y - charge.position.y
            val d = dx * dx + dy * dy
            val factor = charge.charge * 1.0 / (d + sqrt(d))
            Ex += factor * dx
            Ey += factor * dy
        }

        return FieldPoint(
            x = point.x,
            y = point.y,
            forceX = f * Ex,
            forceY = f * Ey
        )
    }

    fun isEmpty() = mutablePointCharges.size <= 0

    companion object Factory {
        /**
         * Create an empty field without charges.
         */
        fun empty(): Field = Field()

        /**
         * A single point charge
         */
        fun monopole(): Field = Field().apply {
            mutablePointCharges.add(createPointCharge(PointF(0f, 0f), -1.0))
        }

        /**
         * A dipole consists of 2 point charges with opposite charge
         */
        fun dipole(): Field = Field().apply {
            val absoluteCharge = 1.0
            mutablePointCharges.apply{
                add(createPointCharge(PointF(0f, 5f), absoluteCharge))
                add(createPointCharge(PointF(0f, -5f), -absoluteCharge))
            }
        }

        /**
         * A quadropole consists of 4 point charges with equal absolute charge
         * that are positioned at the corners of a square such that
         * neighbouring point charges have opposite charge
         */
        fun quadropole(): Field = Field().apply {
            val absoluteCharge = 1.0
            mutablePointCharges.apply{
                add(createPointCharge(PointF(5f, 5f), absoluteCharge))
                add(createPointCharge(PointF(-5f, -5f), absoluteCharge))
                add(createPointCharge(PointF(5f, -5f), -absoluteCharge))
                add(createPointCharge(PointF(-5f, 5f), -absoluteCharge))
            }
        }

        private fun createPointCharge(position: PointF, charge: Double) =
            PointCharge(position = position, charge = charge)
    }
}
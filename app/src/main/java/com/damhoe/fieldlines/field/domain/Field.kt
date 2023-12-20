package com.damhoe.fieldlines.field.domain

import android.graphics.PointF
import com.damhoe.fieldlines.app.Vector
import com.damhoe.fieldlines.app.exception.PointsTooCloseException
import com.damhoe.fieldlines.charges.domain.PointCharge
import com.damhoe.fieldlines.field.application.times
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

    private fun isPositionOccupied(point: Vector, exceptId: Long = -1) =
        mutablePointCharges.filterNot { it.id == exceptId }.any { isNear(it.position, point) }

    private fun isNear(p1: Vector, p2: Vector): Boolean {
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
        newX: Double,
        newY: Double,
        newCharge: Double
    ): Result<Unit> = mutablePointCharges.getOrNull(position)?.run {
        if (isPositionOccupied(Vector(newX, newY), id)) {
            return failure(PointsTooCloseException("At this point, a charge already exists"))
        }
        this.position = Vector(newX, newY)
        charge = newCharge
        success(Unit)
    } ?: failure(IndexOutOfBoundsException(
        "Position $position exceeds number of point charges")
    )

    /**
     * The force corresponds to field strength in
     * appropriate units.
     */
    fun calculateForce(point: Vector): Vector {
        // Start with an empty field
        val force = Vector(x = 0.0, y = 0.0)

        for (charge in pointCharges) {
            val dx = point.x - charge.position.x
            val dy = point.y - charge.position.y
            val d = dx * dx + dy * dy
            val factor = charge.charge * 1.0 / (d + sqrt(d))
            force.apply { x += factor * dx; y += factor * dy }
        }

        val absoluteForce = force.x * force.x + force.y * force.y
        return  (1.0/absoluteForce).times(force)
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
            mutablePointCharges.add(createPointCharge(Vector(0.0, 0.0), -1.0))
        }

        /**
         * A dipole consists of 2 point charges with opposite charge
         */
        fun dipole(): Field = Field().apply {
            val absoluteCharge = 1.0
            mutablePointCharges.apply{
                add(createPointCharge(Vector(0.0, 5.0), absoluteCharge))
                add(createPointCharge(Vector(0.0, -5.0), -absoluteCharge))
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
                add(createPointCharge(Vector(5.0, 5.0), absoluteCharge))
                add(createPointCharge(Vector(-5.0, -5.0), absoluteCharge))
                add(createPointCharge(Vector(5.0, -5.0), -absoluteCharge))
                add(createPointCharge(Vector(-5.0, 5.0), -absoluteCharge))
            }
        }

        private fun createPointCharge(position: Vector, charge: Double) =
            PointCharge(position = position, charge = charge)
    }
}
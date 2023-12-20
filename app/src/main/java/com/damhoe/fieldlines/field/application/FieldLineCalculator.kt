package com.damhoe.fieldlines.field.application

import android.graphics.RectF
import com.damhoe.fieldlines.field.domain.Field
import com.damhoe.fieldlines.app.Vector
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.atan2

class FieldLineCalculator(val field: Field) {
    private val maxIterations: Int = 10_000
    private val integrator: Integrator = MidpointIntegrator()
    var stepSize: Double = 1e-2

    // Area of the field for which charges and field lines are drawn.
    // This area depends on the current zoom and translation and needs to
    // be updated before drawing the field.
    var space = RectF(0f, 0f, 0f, 0f)
    var negativeChargeIntersections: MutableList<MutableList<Double>> = mutableListOf()

    // Points that lay within a circle with this radius
    // around a charge are considered to intersect the charge.
    // Should be updated based on the current transform
    var thresholdDistance: Double = 1.0

    init {
        // Initialize the list
        val negativeChargesCount = field.pointCharges.filter { it.charge < 0 }.size
        repeat(negativeChargesCount) { negativeChargeIntersections.add(mutableListOf()) }
    }

    fun calculateLine(
        start: Vector,
        direction: Double
    ): Sequence<Vector> {
        return generateSequence(start) { currentPoint ->
            integrator.step(currentPoint, stepSize, field::calculateForce, direction)
                .takeUnless { evaluateStopConditions(it, direction) }
        }.take(maxIterations)
    }

    private fun evaluateStopConditions(point: Vector, direction: Double): Boolean {
        /**
         * Two stop conditions are evaluated.
         * (i) The point is located outside of the space
         * (ii) The point is near a charge with opposite charge: In this case
         *      the intersection angle with the charge is stored for future
         *      estimation of missing field lines
         */
        if (isNotInSpace(point)) {
            return true
        }

        return tryCalculateChargeIntersection(point, direction)
    }

    private fun isNear(dx: Double, dy: Double): Boolean {
        return dx * dx + dy * dy < thresholdDistance
    }

    private fun calculateIntersectionAngle(dx: Double, dy: Double): Double {
        return atan2(dy, dx).let { if (it < 0) 2 * PI - it else it }
    }

    private fun tryCalculateChargeIntersection(point: Vector, direction: Double): Boolean {
        /**
         * Returns if the point is close to an opposite charge and
         * calculate the angle of intersection if the intersected charge is negative.
         */

        // Direction specifies the direction of the field line and thus
        // corresponds to the sign of the origin's charge
        // Targets have opposite charge q1 * -q1 < 0
        val targets = field.pointCharges.filter { it.charge * direction < 0 }

        targets.forEachIndexed { i, charge ->
            val dy = point.y - charge.position.y
            val dx = point.x - charge.position.x
            if (isNear(dx, dy)) {
                if (charge.charge < 0)  {
                    val angle = calculateIntersectionAngle(dx, dy)
                    negativeChargeIntersections[i].add(angle)
                }
                return true
            }
        }
        return false
    }

    private fun isNotInSpace(point: Vector) = with(point) {
        (x !in space.left..space.right) ||
                (y !in space.bottom..space.top)
    }
}
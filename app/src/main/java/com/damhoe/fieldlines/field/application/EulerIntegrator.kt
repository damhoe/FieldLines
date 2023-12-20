package com.damhoe.fieldlines.field.application

import com.damhoe.fieldlines.app.Vector

class EulerIntegrator : Integrator {
    override fun step(
        point: Vector,
        stepSize: Double,
        force: (Vector) -> Vector,
        direction: Double
    ): Vector {
        /**
         * Euler method: y_n+1 = y_n + h f (y_n)
         */
        val step = stepSize * direction
        return point.add(step.times(force(point)))
    }
}
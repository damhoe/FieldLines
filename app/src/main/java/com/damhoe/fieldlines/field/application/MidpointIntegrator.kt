package com.damhoe.fieldlines.field.application

import com.damhoe.fieldlines.app.Vector

class MidpointIntegrator : Integrator {
    override fun step(
        point: Vector,
        stepSize: Double,
        force: (Vector) -> Vector,
        direction: Double
    ): Vector {
        /**
         * Midpoint method: y_n+1 = y_n + h f(y_n + h/2 f(y_n))
         */
        val step = stepSize * direction
        val fn = force(point)
        val fModified = force(point.add((step/2).times(fn)))
        return point.add(step.times(fModified))
    }
}
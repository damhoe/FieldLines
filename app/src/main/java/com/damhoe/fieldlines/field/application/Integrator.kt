package com.damhoe.fieldlines.field.application

import com.damhoe.fieldlines.app.Vector

interface Integrator {
    fun step(point: Vector, stepSize: Double, force: (Vector) -> Vector, direction: Double): Vector
}
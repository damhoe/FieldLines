package com.damhoe.fieldlines.field.application

import com.damhoe.fieldlines.app.Vector

fun Vector.add(vector: Vector): Vector = apply { x += vector.x; y += vector.y }

fun Double.times(vector: Vector): Vector = vector.apply { x *= this@times; y *= this@times }
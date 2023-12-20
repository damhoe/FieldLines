package com.damhoe.fieldlines.field.domain

import com.damhoe.fieldlines.app.Vector

/**
 * Each start point is located near a charge.
 * The direction is saved so that the field line only needs
 * to be drawn in one direction.
 */
data class StartPoint(var coordinates: Vector, var direction: Double)
package com.damhoe.fieldlines.field.domain

import android.graphics.PointF

/**
 * Each start point is located near a charge.
 * The direction is saved so that the field line only needs
 * to be drawn in one direction.
 */
data class StartPoint(val coordinates: PointF, val direction: Int, val distanceSquared: Float)
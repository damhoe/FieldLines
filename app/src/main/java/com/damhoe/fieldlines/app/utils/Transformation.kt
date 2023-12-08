package com.damhoe.fieldlines.app.utils

import android.graphics.PointF

/**
 * Created by damian on 07.12.2017.
 */
class Transformation {
    private val SQUARED_PIXEL_LINE_SEGMENT_DISTANCE = 50.0

    var width: Int = 0
    var height: Int = 0
    var viewportWidth: Float = 0f
    var viewportHeight: Float = 0f
    var viewportOffsetX: Float = 0f
    var viewportOffsetY: Float = 0f
    var squaredLineSegmentDistance = 1.0

    val ratioX: Float
        get() = viewportWidth / width

    val ratioY: Float
        get() = viewportHeight / height

    fun resetZoom(defaultViewportWidthRadius: Float) {
        /**
         * Reset the zoom of the transformation object by setting
         * the viewport measures. The height is given by the width and
         * the equal distance constraint for both x and y dimension.
         */
        viewportWidth = defaultViewportWidthRadius * 2
        viewportHeight = if (width > 0f) {
            viewportWidth * height / width
        } else {
            viewportWidth
        }
    }

    fun toPixelX(x: Double): Float {
        return width  * ( 0.5f + (x.toFloat() - viewportOffsetX) / viewportWidth)
    }

    fun toPixelX(x: Float): Float {
        return width  * ( 0.5f + (x - viewportOffsetX) / viewportWidth)
    }

    fun toPixelY(y: Double): Float {
        return height * (0.5f - (y.toFloat() - viewportOffsetY) / viewportHeight)
    }

    fun toPixelY(y: Float): Float {
        return height * (0.5f - (y - viewportOffsetY) / viewportHeight)
    }

    fun toRealX(x: Float): Double {
        return viewportWidth * (x / width - 0.5) + viewportOffsetX
    }

    fun toRealY(y: Float): Double {
        return viewportOffsetY - viewportHeight * (y / height - 0.5)
    }
}
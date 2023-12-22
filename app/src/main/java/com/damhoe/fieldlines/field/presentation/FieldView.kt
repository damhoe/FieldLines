package com.damhoe.fieldlines.field.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import androidx.fragment.app.findFragment
import com.damhoe.fieldlines.app.Vector
import com.damhoe.fieldlines.field.domain.Field
import com.damhoe.fieldlines.field.application.FieldVisualizer
import com.damhoe.fieldlines.app.utils.MathUtils
import com.damhoe.fieldlines.app.utils.Transformation
import com.damhoe.fieldlines.app.utils.GraphicUtils
import com.damhoe.fieldlines.field.application.VisualizerOptions
import com.example.fieldlines.R
import com.google.android.material.color.MaterialColors
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Created by damian on 25.11.2017.
 */
class FieldView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    val options = VisualizerOptions()

    private val defaultViewportRadius: Float = 11f
    private val transformation = Transformation().apply {
        viewportHeight = defaultViewportRadius * 2
        viewportWidth = defaultViewportRadius * 2
    }

    private var field = Field.empty()
    private val fieldVisualizer =
        FieldVisualizer(this, field, transformation, options.maxLinesCount)

    @ColorInt
    private val defaultAxisColor: Int =
        MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnSurface)
    private val axisPaint = Paint().apply {
        strokeWidth = 4f
        color = defaultAxisColor
        style = Paint.Style.STROKE
    }

    private val tickLabelPaint = Paint().apply {
        typeface = Typeface.DEFAULT
        textSize = GraphicUtils.dpToPx(10f).toFloat()
        color = defaultAxisColor
    }

    private val tickLabelHeight: Float = tickLabelPaint.let {
        val rect = Rect()
        it.getTextBounds("-0123456789.", 0, 12, rect)
        rect.height().toFloat()
    }

    private val tickLabelOffset = GraphicUtils.dpToPx(8f)
    private val tickRadius: Float = GraphicUtils.dpToPx(4f).toFloat()
    private val minTickDistance: Float = GraphicUtils.dpToPx(36f).toFloat()
    private val normalizedMinTickIncrement = 0.5f // Normalized to (0.1, 1.0] interval

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.FieldLinesView,
            0, 0).apply {

            try {
                with(getColor(R.styleable.FieldLinesView_axisColor, defaultAxisColor)) {
                    axisPaint.color = this
                    tickLabelPaint.color = this
                }
                with(getColor(R.styleable.FieldLinesView_fieldLinesColor, defaultAxisColor)) {
                    fieldVisualizer.fieldLinePaint.color = this
                }
            } finally {
                recycle()
            }
        }
    }

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        val damping: Float = 1.0f
        val minViewportWidthRadius: Float = 0.11f
        val maxViewportWidthRadius: Float = 11_000f
        var minWidth: Float = 2 * minViewportWidthRadius
        var maxWidth: Float = 2 * maxViewportWidthRadius
        var minHeight: Float = 0f
        var maxHeight: Float = 0f
        var viewportFocusX: Double = 0.0
        var viewportFocusY: Double = 0.0

        /**
         * Save the focus point of the transformation
         * for compensatory translation
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            with(transformation) {
                viewportFocusX = toRealX(detector.focusX)
                viewportFocusY = toRealY(detector.focusY)
            }
            // Calculate zoom constraints that depend on canvas metrics
            minHeight = minWidth * transformation.height / transformation.width
            maxHeight = maxWidth * transformation.height / transformation.width
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val scale: Float = 1f / detector.scaleFactor
            val dampedScale = scale.pow(damping)

            val targetWidth = transformation.viewportWidth * dampedScale
            val targetHeight = transformation.viewportHeight * dampedScale
            // Check for zoom constraints
            val newWidth = max(minWidth, min(targetWidth, maxWidth))
            val newHeight = max(minHeight, min(targetHeight, maxHeight))

            // Apply zoom by updating viewport width and height
            transformation.apply{
                viewportWidth = newWidth
                viewportHeight = newHeight
            }

            val viewportWidth = transformation.viewportWidth
            val viewportHeight = transformation.viewportHeight
            val focusX = detector.focusX
            val focusY = detector.focusY
            val viewportOffsetX = transformation.viewportOffsetX
            val viewportOffsetY = transformation.viewportOffsetY
            val relFocusX = focusX / transformation.width
            val relFocusY = focusY / transformation.height

            // Account for stable viewport focus
            val viewportDistX: Float =
                viewportFocusX.toFloat() - viewportWidth * (relFocusX - 0.5f) - viewportOffsetX
            val viewportDistY: Float =
                +viewportFocusY.toFloat() + viewportHeight * (relFocusY - 0.5f) - viewportOffsetY

            translate(viewportDistX, viewportDistY)

            // Update display
            ViewCompat.postInvalidateOnAnimation(this@FieldView)

            viewportFocusX = transformation.toRealX(focusX)
            viewportFocusY = transformation.toRealY(focusY)
            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)

    private val gestureListener = object : SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            centerAndResetScale()
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            /**
             * Calculate the translated distance in image coordinates.
             */
            val dX = distanceX * transformation.viewportWidth / transformation.width
            val dY = -distanceY * transformation.viewportHeight / transformation.height

            translate(dX, dY)

            // Update the display
            ViewCompat.postInvalidateOnAnimation(this@FieldView)
            return true
        }

        override fun onSingleTapConfirmed(event: MotionEvent): Boolean {

            val point: Vector = transformation.run {
                val x = toRealX(event.x)
                val y = toRealY(event.y)
                Vector(x, y)
            }

            // Check if any charge is close to the location
            findCloseChargePosition(point)
                .takeIf { it >= 0 } // If a charge was found
                ?.let {
                    // Open edit charge dialog if charge is selected
                    val charge = field.pointCharges[it]
                    (findFragment() as FieldFragment).showEditChargeDialog(it, charge)
                }

            return true
        }

        override fun onLongPress(event: MotionEvent) {
            val point: Vector = transformation.run {
                val x = toRealX(event.x).roundToInt().toDouble()
                val y = toRealY(event.y).roundToInt().toDouble()
                Vector(x, y)
            }

            (findFragment() as FieldFragment).startCreateChargeDialog(point)
        }
    }

    private val gestureDetector = GestureDetector(context, gestureListener)

    private fun translate(dX: Float, dY: Float) {
        /**
         * The view maps to a open space, so there is
         * no need to account for boundaries
         */
        transformation.viewportOffsetX += dX
        transformation.viewportOffsetY += dY
    }

    fun centerAndResetScale() {
        transformation.apply {
            viewportOffsetX = 0f
            viewportOffsetY = 0f
            resetZoom(defaultViewportRadius)
        }

        // Update display
        ViewCompat.postInvalidateOnAnimation(this@FieldView)
    }

    fun updateField(newField: Field) {
        field = newField
        fieldVisualizer.field = newField
        ViewCompat.postInvalidateOnAnimation(this) // Trigger refresh of display
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        transformation.apply {
            width = w
            height = h
            resetZoom(defaultViewportRadius)
        }
    }

    private fun findCloseChargePosition(point: Vector): Int {
        field.pointCharges.forEachIndexed { i, charge ->
            if (isNearTap(point, charge.position)) {
                return i
            }
        }

        return -1
    }

    private fun isNearTap(tapPoint: Vector, anchor: Vector): Boolean {
        val dx = tapPoint.x - anchor.x
        val dy = tapPoint.y - anchor.y
        return dx * dx + dy * dy < 2.5e3 * transformation.ratioX * transformation.ratioX
    }

    override fun onDraw(canvas: Canvas) {
        if (options.showAxes) {
            drawAxes(canvas)
        }

        // Draw the field lines and point charges
        fieldVisualizer.apply {
            transform = transformation
            maxNumberOfLines = options.maxLinesCount
        }.draw(canvas)
    }

    private fun drawAxes(canvas: Canvas) {
        val y0: Float = transformation.toPixelY(0.0)
        val x0: Float = transformation.toPixelX(0.0)
        val width: Float = transformation.width.toFloat()
        val height: Float = transformation.height.toFloat()

        canvas.run {
            // Draw x axis if it is inside the viewport
            if (y0 in 0f..height) {
                drawLine(0f, y0, width, y0, axisPaint)

                // Draw x ticks
                val ratio = transformation.ratioX
                val leftX = transformation.toRealX(0f)
                val rightX = transformation.toRealX(width)
                val (exponent, realTicks) = calculateTickPositions(leftX, rightX, ratio)
                val ticks = realTicks.map { transformation.toPixelX(it) }

                for ((realX, x) in realTicks.zip(ticks)) {
                    if (abs(realX) < 1e-6) {
                        continue
                    }
                    drawLine(x, y0 - tickRadius, x, y0 + tickRadius, axisPaint)
                    // Add label
                    val digits = (-exponent + 1).takeIf { it > 0 } ?: 0
                    val tickLabel = String.format("%.${digits}f", realX)
                    val posX = x - tickLabelPaint.measureText(tickLabel) / 2
                    val posY = y0 + tickLabelHeight + tickLabelOffset
                    drawText(tickLabel, posX, posY, tickLabelPaint)
                }
            }

            // Draw y axis
            if (x0 in 0f..width) {
                drawLine(x0, 0f, x0, height, axisPaint)

                // Draw y ticks
                val ratio = transformation.ratioY
                val topY = transformation.toRealY(0f)
                val bottomY = transformation.toRealY(height)
                val (exponent, realTicks) = calculateTickPositions(bottomY, topY, ratio)
                val ticks = realTicks.map { transformation.toPixelY(it) }

                for ((realY, y) in realTicks.zip(ticks)) {
                    if (abs(realY) < 1e-6) {
                        continue
                    }
                    drawLine(x0 - tickRadius, y, x0 + tickRadius, y, axisPaint)
                    // Add label
                    val digits = (-exponent + 1).takeIf { it > 0 } ?: 0
                    val tickLabel = String.format("%.${digits}f", realY)
                    val posX = x0 - tickLabelPaint.measureText(tickLabel) - tickLabelOffset
                    val posY = y + tickLabelHeight / 2
                    drawText(tickLabel, posX, posY, tickLabelPaint)
                }

            }
        }
    }

    /**
     * Estimate the tick positions based on the minimum tick distance
     * and the normalized tick increment. The increment is normalized to the
     * interval (0.1, 1.0]
     */
    private fun calculateTickPositions(min: Double, max: Double, ratio: Float): Pair<Int, List<Double>> {
        val realMinDist = minTickDistance * ratio
        val exponent = ceil(log10(realMinDist)).toInt()
        val minTickIncrement = normalizedMinTickIncrement * 10.0.pow(exponent)
        val increment = MathUtils.roundUpToNearest(realMinDist.toDouble(), minTickIncrement)
        val firstTick = MathUtils.roundUpToNearest(min, increment)
        val ticks = generateSequence(firstTick) {
                x -> (x + increment).takeIf { it <= max }
        }
        return Pair(exponent, ticks.toList())
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }
}
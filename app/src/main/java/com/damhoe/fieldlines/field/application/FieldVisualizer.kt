package com.damhoe.fieldlines.field.application

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt
import com.damhoe.fieldlines.app.utils.Transformation
import com.damhoe.fieldlines.charges.domain.PointCharge
import com.damhoe.fieldlines.field.domain.Field
import com.damhoe.fieldlines.field.domain.StartPoint
import com.damhoe.fieldlines.app.Vector
import com.example.fieldlines.R
import com.google.android.material.R.attr
import com.google.android.material.color.MaterialColors
import java.lang.Math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin

class FieldVisualizer(
    var view: View,
    var field: Field,
    var transform: Transformation,
    var maxNumberOfLines: Int // Number of field line for max charge, the amount of
                                // field line of other charges is scaled by q / qMax
) {
    companion object {
        const val TWO_PI = 2.0 * PI
    }

    @ColorInt
    private val colorPositiveCharge = MaterialColors.getColor(view, R.attr.colorPositiveCharge)
    @ColorInt
    private val colorNegativeCharge = MaterialColors.getColor(view, R.attr.colorNegativeCharge)
    @ColorInt
    private val colorFieldLine = MaterialColors.getColor(view, attr.colorOnSurface)

    private val pointChargePaint = Paint().apply {
        style = Paint.Style.FILL
    }

    val fieldLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = colorFieldLine
    }

    // Area of the field for which charges and field lines are drawn.
    // This area depends on the current zoom and translation and needs to
    // be updated before drawing the field.
    private var space = RectF(0f, 0f, 0f, 0f)
    // Determines the extension of the space outside the viewport
    private var extensionFactor = 2.0f

    // Point charge
    private val pointChargeRadius = 30f
    private val startPointRadius = 10.0

    private val calculatedToDrawnPointsRatio = 2

    private val path: Path = Path()

    private var startPoints: MutableList<StartPoint> = mutableListOf()
    private lateinit var positiveCharges: List<PointCharge>
    private lateinit var negativeCharges: List<PointCharge>
    private var negativeChargeIntersections: MutableList<MutableList<Double>> = mutableListOf()

    /**
     * Visualize the field
     */
    fun draw(canvas: Canvas) {
        // Initialize
        if (startPoints.isNotEmpty()) {
            startPoints.clear()
        }

        if (negativeChargeIntersections.isNotEmpty()) {
            negativeChargeIntersections.clear()
        }

        // Check if charges are present
        if (field.isEmpty()) {
            return
        }

        negativeCharges = field.pointCharges.filter { it.charge < 0 }
        positiveCharges = field.pointCharges.filter { it.charge > 0 }

        repeat(negativeCharges.size) { negativeChargeIntersections.add(mutableListOf()) }

        // Update space
        val width = transform.width
        val height = transform.height
        val extensionX = width * extensionFactor * 0.5f
        val extensionY = height * extensionFactor * 0.5f
        space.apply {
            left = transform.toRealX(0f - extensionX).toFloat()
            right = transform.toRealX(width + extensionX).toFloat()
            top = transform.toRealY(0f - extensionY).toFloat()
            bottom = transform.toRealY(height + extensionY).toFloat()
        }

        drawFieldLines(canvas)
        drawPointCharges(canvas)
    }

    private fun drawPointCharges(canvas: Canvas) {
        for (pointCharge in field.pointCharges) {
            drawPointCharge(pointCharge, canvas)
        }
    }

    /**
     * Point charges are visualized by colored circles with a
     * scale invariant radius.
     */
    private fun drawPointCharge(pointCharge: PointCharge, canvas: Canvas) {
        val realRadius = pointChargeRadius * transform.ratioX

        if (isCircleInViewport(pointCharge.position, realRadius)) {
            val x = transform.toPixelX(pointCharge.position.x.toDouble())
            val y = transform.toPixelY(pointCharge.position.y.toDouble())

            pointChargePaint.apply {
                color = if (pointCharge.charge > 0) {
                    colorPositiveCharge
                } else {
                    colorNegativeCharge
                }
            }
            canvas.drawCircle(x, y, pointChargeRadius, pointChargePaint)
        }
    }

    private fun drawFieldLines(canvas: Canvas) {
        /**
         * Since lines from sources never intersect they can not be drawn twice
         * when starting from different charges.
         * First, draw the field lines for all positive charges.
         */
        val distanceToCenter = startPointRadius * transform.ratioX // ratio X = ratio Y
        val maxCharge = field.maxPointCharge().charge
        var startPointsPositiveCharges: Sequence<StartPoint> = sequenceOf()

        val calculator = FieldLineCalculator(field)
            .apply {
                space = this@FieldVisualizer.space
                thresholdDistance = distanceToCenter
                stepSize = 5e-1 * transform.ratioX
            }

        for (pointCharge in positiveCharges) {
            val numberOfLines = (maxNumberOfLines * abs(pointCharge.charge / maxCharge))
                .roundToInt()

            val startPoints = generateStartPoints(pointCharge, numberOfLines, distanceToCenter)

            startPoints.forEach {
                // Calculate the line = sequence of Vectors
                val line = calculator.calculateLine(it.coordinates, it.direction)
                // Draw the line
                drawLine(line, canvas)
            }
        }

        negativeCharges.forEachIndexed { index, pointCharge ->
            val numberOfLines = (maxNumberOfLines * abs(pointCharge.charge / maxCharge))
                .roundToInt()
            val missingAngles = getMissingStartPointAngles(
                index = index,
                numberOfLines = numberOfLines,
                intersections = calculator.negativeChargeIntersections
            )
            val startPoints = generateStartPoints(pointCharge, missingAngles, distanceToCenter)
            startPoints.forEach {
                val line = calculator.calculateLine(it.coordinates, it.direction)
                drawLine(line, canvas)
            }
        }
    }

    private fun drawLine(points: Sequence<Vector>, canvas: Canvas) {
        // Reset to empty path
        path.reset()

        // Create the path
        val start = points.firstOrNull() ?: return

        with(transform) {
            // Move to first point
            path.moveTo(toPixelX(start.x), toPixelY(start.y))
            // Make path
            points.forEach { path.lineTo(toPixelX(it.x), toPixelY(it.y)) }
        }

        canvas.drawPath(path, fieldLinePaint)
    }

    private fun getMissingStartPointAngles(
        index: Int,
        numberOfLines: Int,
        intersections: List<List<Double>>
    ) : Sequence<Double> {
        /**
         * Calculate the angles for that field lines are missing
         * for a negative charge.
         *
         * If positive charges exist in the space
         * the probability that some field lines end at negative charges
         * is high since these act as sinks.
         */
        val gapAngle = TWO_PI / numberOfLines
        val startAngle = 0.0
        return generateSequence(startAngle) { it + gapAngle }
            .take(numberOfLines)
            .filter { phi -> intersections[index].none { abs(it - phi) < gapAngle * 0.8 } }
    }

    private fun generateStartPoints(
        center: PointCharge,
        numberOfLines: Int,
        distanceToCenter: Double
    ): Sequence<StartPoint> {
        // Distance between start points
        val gapAngle = TWO_PI / numberOfLines
        val startAngle = 0.0
        return generateSequence(startAngle) { it + gapAngle }
            .take(numberOfLines)
            .map {
                center.position.run {
                    val rx = x + distanceToCenter * cos(it)
                    val ry = y + distanceToCenter * sin(it)
                    StartPoint(coordinates = Vector(rx, ry), direction = sign(center.charge))
                }
            }
    }

    private fun generateStartPoints(
        center: PointCharge,
        angles: Sequence<Double>,
        distanceToCenter: Double
    ) : Sequence<StartPoint> {
        return angles.map {
            center.run {
                val x = position.x + distanceToCenter * cos(it)
                val y = position.y + distanceToCenter * sin(it)
                StartPoint(coordinates = Vector(x, y), direction = sign(center.charge))
            }
        }
    }

    private fun isCircleInViewport(center: Vector, radius: Float): Boolean {
        return true
    }
}
package com.damhoe.fieldlines.field.application

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.view.View
import androidx.annotation.ColorInt
import com.damhoe.fieldlines.app.utils.Transformation
import com.damhoe.fieldlines.field.domain.FieldPoint
import com.damhoe.fieldlines.charges.domain.PointCharge
import com.damhoe.fieldlines.field.domain.Field
import com.damhoe.fieldlines.field.domain.StartPoint
import com.example.fieldlines.R
import com.google.android.material.R.attr
import com.google.android.material.color.MaterialColors
import java.lang.Math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sign
import kotlin.math.sin
import kotlin.math.sqrt

class FieldVisualizer(
    var view: View,
    var field: Field,
    var transform: Transformation,
    var maxNumberOfLines: Int // Number of field line for max charge, the amount of
                                // field line of other charges is scaled by q / qMax
) {

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
    private val startPointRadius = 10f
    private val halfLineSegmentLength = 10f
    private val maxSegmentsPerLine = 1_000
    private val calculatedToDrawnPointsRatio = 2

    private var startPoints: MutableList<StartPoint> = mutableListOf()
    private var visitedStartPoints: MutableList<StartPoint> = mutableListOf()

    /**
     * Visualize the field
     */
    fun draw(canvas: Canvas) {
        // Initialize
        if (startPoints.isNotEmpty()) {
            startPoints.clear()
        }

        if (visitedStartPoints.isNotEmpty()) {
            visitedStartPoints.clear()
        }

        // Check if charges are present
        if (field.isEmpty()) {
            return
        }

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
        // Get start points for the field lines and
        // connect them to the point charge center
        val maxCharge = field.maxPointCharge().charge
        for (pointCharge in field.pointCharges) {
            val numberOfLines =
                (maxNumberOfLines * abs(pointCharge.charge / maxCharge)).roundToInt()
            val points = getStartPoints(pointCharge, numberOfLines)
            startPoints.addAll(points)

            // Connect start points with center
            points.forEach { drawConnection(canvas, pointCharge.position, it.coordinates) }
        }

        for (p in startPoints) {
            if (p !in visitedStartPoints) {
                drawFieldLineNew(canvas, p)
            }
        }
    }

    private fun drawConnection(canvas: Canvas, p1: PointF, p2: PointF) {
        with(transform) {
            val x1 = toPixelX(p1.x)
            val x2 = toPixelX(p2.x)
            val y1 = toPixelY(p1.y)
            val y2 = toPixelY(p2.y)
            canvas.drawLine(x1, y1, x2, y2, fieldLinePaint)
        }
    }

    private fun getStartPoints(center: PointCharge, numberOfLines: Int): List<StartPoint> {
        val distanceToCenter = startPointRadius * transform.ratioX // ratio X = ratio Y

        // Calculate Distance of start points
        val phi0 = 2.0 * PI / numberOfLines

        return generateSequence(0.0) { it + phi0 }
            .take(numberOfLines)
            .map {
                center.position.run {
                    val x = x + (distanceToCenter * sin(it).toFloat())
                    val y = y + distanceToCenter * cos(it).toFloat()
                    val d = 2 * sin(phi0).toFloat() * distanceToCenter
                    StartPoint(
                        coordinates = PointF(x, y),
                        direction = sign(center.charge).toInt(),
                        distanceSquared = d * d
                    )
                }
            }
            .toList()
    }

    private val path: Path = Path()
    private val minCalculatedPoints = 10;

    private fun drawFieldLineNew(canvas: Canvas, startPoint: StartPoint) {
        /**
         * Create a new path and draw it afterwards.
         */

        path.reset()

        var fieldPoint = field.calculateFieldPoint(startPoint.coordinates)
        drawSegment(path, fieldPoint)

        drawing@ for (n in 0..maxSegmentsPerLine) {

            for (i in 1..calculatedToDrawnPointsRatio) {
                val nextPoint = field
                    .moveAlongFieldLine(fieldPoint, transform.ratioX, startPoint.direction)

                if (isNotInSpace(nextPoint)) {
                    break@drawing
                }

                if ((n > minCalculatedPoints) and isNearStartPoint(nextPoint)) {
                    break@drawing
                }

                fieldPoint = field.calculateFieldPoint(nextPoint)
            }

            drawSegment(path, fieldPoint)
        }

        // Draw path
        canvas.drawPath(path, fieldLinePaint)
    }

    /**
     * Draw a segment of a field line at a given field point.
     */
    private fun drawSegment(path: Path, anchor: FieldPoint) {
        /**
         * The segment is parallel to the field vector at the anchor point
         * which is located at the center of the line. The length of the segment
         * only depends on the pixel resolution of the field lines.
         */
        val fx: Float
        val fy: Float
        with (anchor) {
            val f = sqrt(forceX * forceX + forceY * forceY)
            fx = (forceX / f).toFloat()
            fy = (forceY / f).toFloat()
        }

        val anchorPixelX = transform.toPixelX(anchor.x)
        val anchorPixelY = transform.toPixelY(anchor.y)
        val dx = halfLineSegmentLength * fx
        val dy = halfLineSegmentLength * fy

        path.run {
            moveTo(anchorPixelX + dx, anchorPixelY - dy)
            lineTo(anchorPixelX - dx, anchorPixelY + dy)
        }
    }

    private fun isNear(startPoint: StartPoint, p: PointF) = with(startPoint) {
        val dx = coordinates.x - p.x
        val dy = coordinates.y - p.y
        (dx * dx + dy * dy) < 1.5 * distanceSquared / 4
    }

    private fun isNearStartPoint(point: PointF): Boolean =
        startPoints.filter { isNear(it, point) }
            .getOrNull(0)
            ?.let { visitedStartPoints.add(it); true }
            ?: false

    private fun isNotInSpace(point: PointF) = with(point) {
        (x !in space.left..space.right) ||
                (y !in space.bottom..space.top)
    }

    private fun isCircleInViewport(center: PointF, radius: Float): Boolean {
        return true
    }
}
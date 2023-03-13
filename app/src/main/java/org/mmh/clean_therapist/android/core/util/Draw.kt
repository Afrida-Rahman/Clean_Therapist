package org.mmh.clean_therapist.android.core.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import org.mmh.clean_therapist.android.core.model.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Draw(
    private val canvas: Canvas,
    private val color: Int,
    private val thickness: Float
) {
    fun line(
        startPoint: Point,
        endPoint: Point,
        lineType: Paint.Style? = Paint.Style.FILL,
        _color: Int = color,
        _thickness: Float = thickness
    ) {
        val lineStyle = Paint().apply {
            strokeWidth = _thickness
            color = _color
            style = lineType
        }
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, lineStyle)
    }

    fun circle(
        center: Point,
        radius: Float,
        vectorBc: Point,
        angleValue: Float,
        clockwise: Boolean = false,
        _color: Int = color
    ) {
        val circleStyle = Paint().apply {
            strokeWidth = thickness
            color = _color
            style = Paint.Style.STROKE
        }
        var startAngle = Utilities.angle(Point(vectorBc.x, -vectorBc.y))
        if (clockwise) {
            startAngle -= angleValue
        }
        val oval = RectF()
        oval.set(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
        canvas.drawArc(oval, -startAngle, -angleValue, true, circleStyle)
    }

    fun writeText(
        text: String,
        position: Point,
        textColor: Int = Color.WHITE,
        fontSize: Float = 30f,
        showBackground: Boolean = false,
        backgroundColor: Int = Color.rgb(0, 0, 0),
    ) {
        val textStyle = Paint().apply {
            color = textColor
            textSize = fontSize
            style = Paint.Style.FILL
        }
        val textWidth = textStyle.measureText(text)
        val fontMetrics = Paint.FontMetrics()
        textStyle.getFontMetrics(fontMetrics)
        val xPosition = position.x
        val yPosition = position.y

        if (showBackground) {
            rectangle(
                xPosition - 10,
                yPosition + fontMetrics.top - 10,
                xPosition + textWidth + 10,
                yPosition + fontMetrics.bottom + 10,
                backgroundColor
            )
        }
        canvas.drawText(text, xPosition, yPosition, textStyle)
    }

    fun angle(
        startPoint: Point,
        middlePoint: Point,
        endPoint: Point,
        lineType: Paint.Style? = Paint.Style.FILL,
        radius: Float = 50F,
        clockwise: Boolean = false
    ) {
        val pointA = Point(startPoint.x, -startPoint.y)
        val pointB = Point(middlePoint.x, -middlePoint.y)
        val pointC = Point(endPoint.x, -endPoint.y)
        val angleValue = Utilities.angle(pointA, pointB, pointC, clockwise).toInt()
        val vectorBc = Point(pointC.x - pointB.x, pointC.y - pointB.y)
        val startAngle = Utilities.angle(vectorBc)
        val endAngle = if (clockwise) {
            startAngle - angleValue
        } else {
            startAngle + angleValue
        }
        var midAngle = if (endAngle > startAngle) {
            endAngle - angleValue / 2
        } else {
            startAngle - angleValue / 2
        }
        midAngle = (midAngle * PI.toFloat()) / 180f
        val textPositionRadius = radius + 70
        val textPosition = Point(
            middlePoint.x + textPositionRadius * cos(midAngle),
            middlePoint.y - textPositionRadius * sin(midAngle)
        )
        val referenceVector = Point(endPoint.x - middlePoint.x, endPoint.y - middlePoint.y)

        line(startPoint, middlePoint, lineType)
        line(middlePoint, endPoint, lineType)

        circle(middlePoint, radius, referenceVector, angleValue.toFloat(), clockwise = clockwise)
        writeText("$angleValue", textPosition)
    }

    fun tetragonal(
        firstPoint: Point,
        secondPoint: Point,
        thirdPoint: Point,
        forthPoint: Point,
        _color: Int = color,
        lineType: Paint.Style? = Paint.Style.FILL,
        _thickness: Float = thickness
    ) {
        line(firstPoint, secondPoint, lineType, _color, _thickness)
        line(secondPoint, thirdPoint, lineType, _color, _thickness)
        line(thirdPoint, forthPoint, lineType, _color, _thickness)
        line(forthPoint, firstPoint, lineType, _color, _thickness)
    }

    private fun rectangle(
        left: Float, top: Float, right: Float, bottom: Float, _color: Int = color,
        lineType: Paint.Style? = Paint.Style.FILL,
        _thickness: Float = thickness
    ) {
        val rectangleStyle = Paint().apply {
            color = _color
            strokeWidth = _thickness
            style = lineType
        }
        canvas.drawRect(left, top, right, bottom, rectangleStyle)
    }
}

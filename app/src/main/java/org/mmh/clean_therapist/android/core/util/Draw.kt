package org.mmh.clean_therapist.android.core.util

import android.graphics.*
import org.mmh.clean_therapist.android.core.model.Point
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Draw(
    val canvas: Canvas,
    private val color: Int,
    private val thickness: Float,
    private val clockWise: Boolean = false
) {
    fun line(
        startPoint: Point,
        endPoint: Point,
        lineType: Paint.Style? = Paint.Style.FILL,
        _color: Int = Color.WHITE,
        _thickness: Float = thickness
    ) {
        val lineStyle = Paint().apply {
            strokeWidth = _thickness
            color = _color
            style = lineType
        }
        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, lineStyle)
        circle(startPoint, 4f, startPoint, 360f, _color = _color)
        circle(endPoint, 4f, endPoint, 360f, _color = _color)
    }

    fun circle(
        center: Point,
        radius: Float,
        vectorBc: Point,
        angleValue: Float,
        _color: Int = color,
        _clockWise: Boolean = clockWise
    ) {
        val circleStyle = Paint().apply {
            strokeWidth = thickness
            color = _color
            style = Paint.Style.STROKE
        }
        var startAngle = Utilities.angle(Point(vectorBc.x, -vectorBc.y))
        if (_clockWise) {
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
        fontSize: Float = 40f,
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
        val xPosition = position.x - textWidth / 2
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
        _clockWise: Boolean = clockWise,
        color: Int = Color.WHITE
    ) {
        val pointA = Point(startPoint.x, -startPoint.y)
        val pointB = Point(middlePoint.x, -middlePoint.y)
        val pointC = Point(endPoint.x, -endPoint.y)
        val angleValue = Utilities.angle(pointA, pointB, pointC, _clockWise).toInt()
        val vectorBc = Point(pointC.x - pointB.x, pointC.y - pointB.y)
        val startAngle = Utilities.angle(vectorBc)
        val endAngle = if (_clockWise) {
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
        val textPositionRadius = radius - 10
        val textPosition = Point(
            middlePoint.x + textPositionRadius * cos(midAngle),
            middlePoint.y - textPositionRadius * sin(midAngle)
        )
        val referenceVector = Point(endPoint.x - middlePoint.x, endPoint.y - middlePoint.y)

        line(startPoint, middlePoint, lineType, _color = color)
        line(middlePoint, endPoint, lineType, _color = color)

        circle(startPoint, 4f, startPoint, 360f, _clockWise = _clockWise, _color = color)
        circle(middlePoint, 4f, middlePoint, 360f, _clockWise = _clockWise, _color = color)
        circle(endPoint, 4f, endPoint, 360f, _clockWise = _clockWise, _color = color)

        circle(
            middlePoint,
            radius,
            referenceVector,
            angleValue.toFloat(),
            _clockWise = _clockWise,
            _color = color
        )
        writeText("$angleValue", textPosition, fontSize = 20f)
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

    // Draws a rectangular shaped with round corners button with a text in the centre
    fun button(rectF: RectF, cornerRadio: Float, color: Int, text: String, textColor: Int, textSize: Float){
        val rectStyle = Paint().apply {
            this.color = color
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 5f
        }
        canvas.drawRoundRect(
            rectF,
            cornerRadio,
            cornerRadio,
            rectStyle
        )
        var textWidth = 0f
        var textSmallGlyphHeight = 0f
        val textStyle = Paint().apply {
            this.color = textColor
            this.textSize = textSize
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            style = Paint.Style.FILL
            textWidth = measureText(text)
            textSmallGlyphHeight = fontMetrics.run { ascent + descent }
        }
        canvas.drawText(text, rectF.left+((rectF.right-rectF.left)-textWidth)/2f, rectF.top+(2*(rectF.right-rectF.left)/3-textSmallGlyphHeight)/2f, textStyle)
    }
}

package org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint


import android.graphics.Paint
import org.mmh.clean_therapist.android.core.model.Point
import org.mmh.clean_therapist.android.core.util.Draw
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.LineType

data class AngleConstraint(
    val startPointIndex: Int,
    val middlePointIndex: Int,
    val endPointIndex: Int,
    val lineType: LineType = LineType.SOLID,
    val minValidationValue: Int,
    val maxValidationValue: Int,
    val isClockwise: Boolean,
    val shouldDrawExtensionFlexion: Boolean
) : Constraint {
    override fun draw(draw: Draw) {
        val lineStyle = when (lineType) {
            LineType.SOLID -> Paint.Style.FILL
            LineType.DASHED -> Paint.Style.FILL_AND_STROKE
        }
        val startPoint = Point(0f, 0f)
        val middlePoint = Point(0f, 0f)
        val endPoint = Point(0f, 0f)
        draw.angle(
            startPoint = startPoint,
            middlePoint = middlePoint,
            endPoint = endPoint,
            lineType = lineStyle,
            clockwise = isClockwise
        )
        draw.circle(startPoint, 4f, startPoint, 360f)
        draw.circle(middlePoint, 4f, middlePoint, 360f)
        draw.circle(endPoint, 4f, endPoint, 360f)
    }
}
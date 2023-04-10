package org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint


import android.graphics.Color
import android.graphics.Paint
import org.mmh.clean_therapist.android.core.util.Draw
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.LineType
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person

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
    override fun draw(draw: Draw, person: Person) {
        var color: Int
        val lineStyle = when (lineType) {
            LineType.SOLID -> Paint.Style.FILL
            LineType.DASHED -> Paint.Style.FILL_AND_STROKE
        }
        val angle = Utilities.angle(
            startPoint = person.keyPoints[startPointIndex].toRealPoint(),
            middlePoint = person.keyPoints[middlePointIndex].toRealPoint(),
            endPoint = person.keyPoints[endPointIndex].toRealPoint(),
            clockWise = !isClockwise
        )
        color = if (angle >= minValidationValue && angle <= maxValidationValue) {
            Color.WHITE
        } else {
            Color.RED
        }
        val startPoint = person.keyPoints[startPointIndex].toCanvasPoint()
        val middlePoint = person.keyPoints[middlePointIndex].toCanvasPoint()
        val endPoint = person.keyPoints[endPointIndex].toCanvasPoint()
        draw.angle(
            startPoint = startPoint,
            middlePoint = middlePoint,
            endPoint = endPoint,
            lineType = lineStyle,
            _clockWise = !isClockwise,
            color = color
        )
        draw.circle(startPoint, 4f, startPoint, 360f)
        draw.circle(middlePoint, 4f, middlePoint, 360f)
        draw.circle(endPoint, 4f, endPoint, 360f)
    }

}
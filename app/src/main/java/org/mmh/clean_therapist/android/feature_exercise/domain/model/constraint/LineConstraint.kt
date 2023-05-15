package org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint

import android.graphics.Paint
import org.mmh.clean_therapist.android.core.model.Point
import org.mmh.clean_therapist.android.core.util.Draw
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.LineType
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person

data class LineConstraint(
    override val startPointIndex: Int,
    override val middlePointIndex: Int = -1,
    override val endPointIndex: Int,
    val lineType: LineType = LineType.SOLID,
    override val minValidationValue: Int,
    override val maxValidationValue: Int,
    override var lowestMinValidationValue: Int,
    override var lowestMaxValidationValue: Int,
    override var storedValues: ArrayList<Int> = ArrayList()
) : Constraint {
    override fun draw(draw: Draw, person: Person) {
        val lineStyle = if (lineType == LineType.SOLID) {
            Paint.Style.FILL
        } else {
            Paint.Style.FILL_AND_STROKE
        }
        val startPoint = Point(0f, 0f)
        val endPoint = Point(0f, 0f)
        draw.line(startPoint = startPoint, endPoint = endPoint, lineType = lineStyle)
        draw.circle(startPoint, 4f, startPoint, 360f)
        draw.circle(endPoint, 4f, endPoint, 360f)
    }
}
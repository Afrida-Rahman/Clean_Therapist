package org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint

import android.graphics.Color
import org.mmh.clean_therapist.android.core.model.Point
import org.mmh.clean_therapist.android.core.util.Draw
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person

data class TextConstraint(
    val text: String,
    val position: Point,
    val textColor: Int = Color.WHITE,
    val fontSize: Float = 30f,
    val showBackground: Boolean = false,
    val backgroundColor: Int = Color.rgb(0, 0, 0),
    override val startPointIndex: Int,
    override val middlePointIndex: Int,
    override val endPointIndex: Int,
    override val minValidationValue: Int,
    override val maxValidationValue: Int,
    override var lowestMinValidationValue: Int,
    override var lowestMaxValidationValue: Int,
    override var storedValues: ArrayList<Int>
) : Constraint {
    override fun draw(draw: Draw, person: Person) {
        draw.writeText(
            text = text,
            position = position,
            textColor = textColor,
            fontSize = fontSize,
            showBackground = showBackground,
            backgroundColor = backgroundColor
        )
    }
}
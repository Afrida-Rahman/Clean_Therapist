package org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint

import android.graphics.Color
import org.mmh.clean_therapist.android.core.model.Point
import org.mmh.clean_therapist.android.core.util.Draw
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint

data class TextConstraint(
    val text: String,
    val position: Point,
    val textColor: Int = Color.WHITE,
    val fontSize: Float = 30f,
    val showBackground: Boolean = false,
    val backgroundColor: Int = Color.rgb(0, 0, 0)
) : Constraint {
    override fun draw(draw: Draw) {
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
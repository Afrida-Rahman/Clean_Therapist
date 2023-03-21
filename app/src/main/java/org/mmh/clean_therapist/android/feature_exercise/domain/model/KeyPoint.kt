package org.mmh.clean_therapist.android.feature_exercise.domain.model

import android.graphics.PointF

data class KeyPoint(
    val bodyPart: BodyPart,
    var coordinate: PointF,
    val score: Float
) {
    fun toRealPoint(): Point {
        return Point(
            coordinate.x,
            -coordinate.y
        )
    }

    fun toCanvasPoint(): Point {
        return Point(
            coordinate.x,
            coordinate.y
        )
    }
}

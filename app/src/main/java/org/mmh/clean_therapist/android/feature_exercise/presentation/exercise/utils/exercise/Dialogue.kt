package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.exercise

data class Dialogue(val distance: Float) {
    fun updateTextSize(): Float {
        val textSize = when {
            distance <= 5f -> 30f
            distance > 5f && distance <= 10f -> 50f
            else -> 70f
        }
        return textSize
    }
}

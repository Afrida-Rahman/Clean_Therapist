package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.exercise

data class Dialogue(val distance: Float) {
    fun updateTextSize(): Float {
        return when {
            this.distance <= 5f -> 30f
            this.distance > 5f && this.distance <= 10f -> 50f
            else -> 70f
        }
    }
}

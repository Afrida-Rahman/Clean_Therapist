package org.mmh.clean_therapist.android.feature_exercise.domain.model

data class ExerciseInfo(val repetitionCount: Int,
                        val setCount: Int,
                        val maxHoldTime: Int,
                        val maxRepCount: Int,
                        val wrongCount: Int,
                        val dialogue: String?,
                        val distance: Float)

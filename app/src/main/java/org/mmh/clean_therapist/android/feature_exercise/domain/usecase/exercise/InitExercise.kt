package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.exercise

import android.content.Context
import org.mmh.clean_therapist.android.feature_exercise.domain.repository.ExerciseRepository

class InitExercise(
    private val repository: ExerciseRepository
) {
    operator fun invoke(context: Context, exerciseId: Int, active: Boolean) {
        repository.initExercise(context, exerciseId, active)
    }
}
package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.exercise

import org.mmh.clean_therapist.android.feature_exercise.domain.repository.ExerciseRepository

class PauseExercise(
    private val repository: ExerciseRepository
) {
    operator fun invoke(): Int {
        return repository.getWrongCount()
    }
}
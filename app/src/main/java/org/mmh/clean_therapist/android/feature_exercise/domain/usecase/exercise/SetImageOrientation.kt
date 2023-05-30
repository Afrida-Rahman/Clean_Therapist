package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.exercise

import org.mmh.clean_therapist.android.feature_exercise.domain.repository.ExerciseRepository

class SetImageOrientation(
    private val repository: ExerciseRepository
) {
    operator fun invoke(imageFlipped: Boolean) {
        repository.setImageOrientation(imageFlipped)
    }
}
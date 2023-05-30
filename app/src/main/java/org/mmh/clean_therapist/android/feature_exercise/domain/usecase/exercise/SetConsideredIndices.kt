package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.exercise

import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.repository.ExerciseRepository

class SetConsideredIndices(
    private val repository: ExerciseRepository
) {
    operator fun invoke(phases: List<Phase>) {
        repository.setConsideredIndices(phases)
    }
}
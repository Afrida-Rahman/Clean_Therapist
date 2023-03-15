package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList

import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise

sealed class ExerciseListEvent {
    data class SaveDataButtonClicked(
        val testId: String,
        val exercise: Exercise,
        val repetitionCount: Int,
        val setCount: Int,
        val wrongCount: Int
    ) :
        ExerciseListEvent()
}

package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList

import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonEvent

sealed class ExerciseListEvent {
    data class FetchExercises(val testId: String, val tenant: String) : ExerciseListEvent()
    data class ApplyExerciseFilter(val testId: String, val exerciseName: String) : ExerciseListEvent()
    data class SaveDataButtonClicked(
        val testId: String,
        val exercise: Exercise,
        val repetitionCount: Int,
        val setCount: Int,
        val wrongCount: Int
    ) :
        ExerciseListEvent()
}

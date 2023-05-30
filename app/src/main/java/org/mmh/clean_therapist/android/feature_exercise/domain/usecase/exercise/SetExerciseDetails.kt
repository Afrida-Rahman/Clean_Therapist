package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.exercise

import org.mmh.clean_therapist.android.feature_exercise.domain.repository.ExerciseRepository

class SetExerciseDetails(
    private val repository: ExerciseRepository
) {
    operator fun invoke(
        exerciseName: String,
        exerciseInstruction: String?,
        exerciseImageUrls: List<String>,
        exerciseVideoUrls: String,
        repetitionLimit: Int,
        setLimit: Int,
        protoId: Int
    ) {
        repository.setExerciseDetails(
            exerciseName = exerciseName,
            exerciseInstruction = "",
            exerciseImageUrls = listOf(),
            exerciseVideoUrls = "",
            repetitionLimit = repetitionLimit,
            setLimit = setLimit,
            protoId = protoId
        )

    }
}
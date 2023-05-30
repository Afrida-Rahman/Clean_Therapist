package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.exercise

data class PerformExerciseUseCase(
    val initExercise: InitExercise,
    val setImageOrientation: SetImageOrientation,
    val setExerciseDetails: SetExerciseDetails,
    val setConsideredIndices: SetConsideredIndices,
    val getMaxHoldTime: GetMaxHoldTime,
    val getRepetitionCount: GetRepetitionCount
)

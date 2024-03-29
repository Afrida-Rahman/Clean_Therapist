package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.networkData

data class ExerciseUseCases(
    val fetchAssessments: FetchAssessments,
    val fetchExercises: FetchExercises,
    val fetchExerciseConstraints: FetchExerciseConstraints,
    val saveExerciseData: SaveExerciseData
)
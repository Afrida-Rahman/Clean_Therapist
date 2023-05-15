package org.mmh.clean_therapist.android.feature_exercise.presentation

sealed class CommonEvent {
    object FetchAssessments : CommonEvent()
    data class FetchExercises(val testId: String, val tenant: String) : CommonEvent()
    data class FetchExerciseConstraints(
        val tenant: String,
        val testId: String,
        val exerciseId: Int
    ) : CommonEvent()

    data class ApplyAssessmentFilter(val testId: String?, val bodyRegion: String?) : CommonEvent()
    data class ApplyExerciseFilter(val testId: String, val exerciseName: String) : CommonEvent()
    object SignOut : CommonEvent()
}

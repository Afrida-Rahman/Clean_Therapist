package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

sealed class ExerciseEvent {
    object FlipCamera : ExerciseEvent()
    object GoToAssessmentPage : ExerciseEvent()
    object ShowPauseBtn : ExerciseEvent()
    object ShowResumeBtn : ExerciseEvent()

}
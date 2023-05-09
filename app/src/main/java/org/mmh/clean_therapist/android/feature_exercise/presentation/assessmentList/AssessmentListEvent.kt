package org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList

sealed class AssessmentListEvent {
    object FetchAssessments : AssessmentListEvent()
    data class ApplyAssessmentFilter(val testId: String?, val bodyRegion: String?) :
        AssessmentListEvent()

    object SignOut : AssessmentListEvent()
}
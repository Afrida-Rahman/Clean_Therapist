package org.mmh.clean_therapist.android.feature_exercise.domain.model

data class Assessment(
    val testId: String,
    val creationDate: String,
    val isReportReady: Boolean,
    val providerId: String?,
    val providerName: String?,
    val bodyRegionId: Int,
    val bodyRegionName: String,
    val registrationType: String,
    val totalExercise: Int,
    var exercises: List<Exercise> = emptyList()
)
package org.mmh.clean_therapist.android.feature_exercise.domain.payload

import com.google.gson.annotations.SerializedName

data class ExerciseListPayload(
    @SerializedName("Tenant") val tenant: String,
    @SerializedName("TestId") val testId: String
)
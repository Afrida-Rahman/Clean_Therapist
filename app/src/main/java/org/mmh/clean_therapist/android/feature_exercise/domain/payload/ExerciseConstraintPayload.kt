package org.mmh.clean_therapist.android.feature_exercise.domain.payload

import com.google.gson.annotations.SerializedName

data class ExerciseConstraintPayload(
    @SerializedName("Tenant") val tenant: String,
    @SerializedName("ExerciseId") val exerciseId: Int
)
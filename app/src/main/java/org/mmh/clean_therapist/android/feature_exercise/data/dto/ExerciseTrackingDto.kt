package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName

data class ExerciseTrackingDto(
    @SerializedName("Successful")
    val success: Boolean,
    @SerializedName("Message")
    val message: String
)
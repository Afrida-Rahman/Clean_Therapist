package org.mmh.clean_therapist.android.feature_exercise.domain.payload

import com.google.gson.annotations.SerializedName

data class ExerciseTrackingPayload(
    @SerializedName("ExerciseId") val exerciseId: Int,
    @SerializedName("TestId") val testId: String,
    @SerializedName("ProtocolId") val protocolId: Int,
    @SerializedName("PatientId") val patientId: String,
    @SerializedName("ExerciseDate") val exerciseDate: String,
    @SerializedName("NoOfReps") val noOfReps: Int,
    @SerializedName("NoOfSets") val noOfSets: Int,
    @SerializedName("NoOfWrongCount") val noOfWrongCount: Int,
    @SerializedName("Tenant") val tenant: String
)
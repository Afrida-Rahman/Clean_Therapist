package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Assessment

data class RemoteAssessment(
    @SerializedName("TestId") val testId: String,
    @SerializedName("BodyRegionId") val bodyRegionId: Int,
    @SerializedName("BodyRegionName") val bodyRegionName: String,
    @SerializedName("ProviderName") val providerName: String,
    @SerializedName("ProviderId") val providerId: String,
    @SerializedName("CreatedOnUtc") val createdOnUtc: String,
    @SerializedName("IsReportReady") val isReportReady: Boolean,
    @SerializedName("RegistrationType") val registrationType: String,
    @SerializedName("TotalExercise") val totalExercise: Int
)

fun RemoteAssessment.toAssessment(): Assessment {
    return Assessment(
        testId = testId,
        creationDate = createdOnUtc.split("T")[0],
        isReportReady = isReportReady,
        providerId = providerId,
        providerName = providerName,
        bodyRegionId = bodyRegionId,
        bodyRegionName = bodyRegionName,
        registrationType = registrationType,
        totalExercise = totalExercise,
        exercises = emptyList(),
    )
}
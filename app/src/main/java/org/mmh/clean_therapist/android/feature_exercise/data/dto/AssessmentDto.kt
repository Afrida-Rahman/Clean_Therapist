package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Assessment

data class AssessmentDto(
    @SerializedName("Assessments") val remoteAssessments: List<RemoteAssessment>
)

fun AssessmentDto.toAssessmentList(): List<Assessment> = remoteAssessments.map {
    it.toAssessment()
}
package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.LineType
import org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint.AngleConstraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint.LineConstraint

data class RemoteConstraint(
    @SerializedName("Scale") val scale: String,
    @SerializedName("LineType") val lineType: String,
    @SerializedName("NoOfKeyPoints") val noOfKeyPoints: Int,
    @SerializedName("Direction") val direction: String,
    @SerializedName("StartKeyPosition") val startKeyPosition: String,
    @SerializedName("MiddleKeyPosition") val middleKeyPosition: String,
    @SerializedName("EndKeyPosition") val endKeyPosition: String,
    @SerializedName("MinValidationValue") val minValidationValue: Int,
    @SerializedName("MaxValidationValue") val maxValidationValue: Int,
    @SerializedName("AngleArea") val angleArea: String,
    @SerializedName("DrawExtensionFlexion") val shouldDrawExtensionFlexion: Boolean
)

fun RemoteConstraint.toConstraint(): Constraint {
    return when (scale) {
        "Angle" -> AngleConstraint(
            startPointIndex = 0,
            middlePointIndex = 0,
            endPointIndex = 0,
            lineType = if (lineType == "solid") {
                LineType.SOLID
            } else {
                LineType.DASHED
            },
            minValidationValue = minValidationValue,
            maxValidationValue = maxValidationValue,
            isClockwise = angleArea == "clockwise",
            shouldDrawExtensionFlexion = shouldDrawExtensionFlexion
        )
        else -> LineConstraint(
            startPointIndex = 0,
            endPointIndex = 0,
            lineType = if (lineType == "solid") {
                LineType.SOLID
            } else {
                LineType.DASHED
            },
            minValidationValue = minValidationValue,
            maxValidationValue = maxValidationValue
        )
    }
}
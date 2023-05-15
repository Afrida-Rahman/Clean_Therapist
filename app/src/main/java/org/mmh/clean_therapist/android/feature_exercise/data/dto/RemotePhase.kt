package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase

data class RemotePhase(
    @SerializedName("PhaseNumber") val id: Int,
    @SerializedName("PhaseDialogue") val dialogue: String?,
    @SerializedName("HoldInSeconds") val holdTime: Int = 0,
    @SerializedName("CapturedImage") val imageUrl: String,
    @SerializedName("Restrictions") val constraints: List<RemoteConstraint> = emptyList()
)

fun RemotePhase.toPhase(): Phase {
    val constraintList: List<Constraint> = if (constraints.isNullOrEmpty()) {
        emptyList()
    } else {
        constraints.map { it.toConstraint() }
    }
    return Phase(
        id = id,
        instruction = dialogue,
        holdTime = holdTime,
        imageUrl = imageUrl,
        constraints = constraintList,
    )
}
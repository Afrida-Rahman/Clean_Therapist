package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase

data class PhaseDto(
    @SerializedName("ExerciseId") val exerciseId: Int,
    @SerializedName("Phases") val phases: List<RemotePhase>
)

fun PhaseDto.toPhaseList(): List<Phase> = phases.map { it.toPhase() }
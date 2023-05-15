package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise

data class RemoteExercise(
    @SerializedName("ExerciseId") val id: Int,
    @SerializedName("ExerciseMedia") val videoUrl: String,
    @SerializedName("ProtocolId") val protocolId: Int,
    @SerializedName("ExerciseName") val name: String,
    @SerializedName("Instructions") val instruction: String,
    @SerializedName("ImageURLs") val imageUrls: List<String>,
    @SerializedName("SetInCount") val totalSet: Int,
    @SerializedName("RepetitionInCount") val repetitionPerSet: Int,
    @SerializedName("FrequencyInDay") val frequency: Int,
    @SerializedName("Phases") val phases: List<RemotePhase>
)

fun RemoteExercise.toExercise(): Exercise {
    return Exercise(
        id = id,
        name = name,
        imageURLs = imageUrls,
        videoURL = videoUrl,
        frequency = frequency,
        repetition = repetitionPerSet,
        set = totalSet,
        protocolId = protocolId,
        instruction = instruction,
        phases = phases.map { it.toPhase() },
    )
}
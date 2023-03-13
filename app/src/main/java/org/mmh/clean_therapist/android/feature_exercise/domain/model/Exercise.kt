package org.mmh.clean_therapist.android.feature_exercise.domain.model

data class Exercise(
    val id: Int,
    val name: String,
    val imageURLs: List<String>,
    val videoURL: String?,
    val frequency: Int,
    val repetition: Int,
    val set: Int,
    val protocolId: Int,
    val instruction: String,
    var phases: List<Phase>
)
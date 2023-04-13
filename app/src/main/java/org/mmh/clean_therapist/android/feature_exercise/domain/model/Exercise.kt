package org.mmh.clean_therapist.android.feature_exercise.domain.model

import com.google.gson.Gson

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
fun <Exercise> String.fromJson(type: Class<Exercise>): Exercise {
    return Gson().fromJson(this, type)
}
fun <Exercise> Exercise.toJson(): String {
    return Gson().toJson(this)
}

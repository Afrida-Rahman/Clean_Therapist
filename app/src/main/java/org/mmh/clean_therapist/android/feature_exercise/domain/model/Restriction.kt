package org.mmh.clean_therapist.android.feature_exercise.domain.model

data class Restriction(
    val StartKeyPosition: String,
    val MiddleKeyPosition: String,
    val EndKeyPosition: String,
    val AverageMin: Int,
    val AverageMax: Int
)

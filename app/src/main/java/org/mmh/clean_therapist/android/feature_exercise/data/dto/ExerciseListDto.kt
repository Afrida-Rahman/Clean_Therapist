package org.mmh.clean_therapist.android.feature_exercise.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise

data class ExerciseListDto(
    @SerializedName("Exercises") val exercises: List<RemoteExercise>
)

fun ExerciseListDto.toExerciseList(): List<Exercise> = exercises.map { it.toExercise() }

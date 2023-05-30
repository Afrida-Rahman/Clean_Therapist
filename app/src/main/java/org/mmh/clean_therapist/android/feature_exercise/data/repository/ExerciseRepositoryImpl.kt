package org.mmh.clean_therapist.android.feature_exercise.data.repository

import android.content.Context
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.Exercises
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.GeneralExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.HomeExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.repository.ExerciseRepository

class ExerciseRepositoryImpl : ExerciseRepository {

    lateinit var homeExercise: HomeExercise

    override fun initExercise(context: Context, exerciseId: Int, active: Boolean) {
        val existingExercise = Exercises.get(context, exerciseId)
        homeExercise = existingExercise ?: GeneralExercise(
            context = context, exerciseId = exerciseId, active = true
        )
    }

    override fun setImageOrientation(imageFlipped: Boolean) {
        homeExercise.setImageFlipped(imageFlipped)
    }

    override fun setExerciseDetails(
        exerciseName: String,
        exerciseInstruction: String?,
        exerciseImageUrls: List<String>,
        exerciseVideoUrls: String,
        repetitionLimit: Int,
        setLimit: Int,
        protoId: Int
    ) {
        homeExercise.setExercise(
            exerciseName = exerciseName,
            exerciseInstruction = "",
            exerciseImageUrls = listOf(),
            exerciseVideoUrls = "",
            repetitionLimit = repetitionLimit,
            setLimit = setLimit,
            protoId = protoId
        )
    }

    override fun setConsideredIndices(phases: List<Phase>) {
        homeExercise.setConsideredIndices(phases)
    }

    override fun getMaxHoldTime(): Int {
        return homeExercise.getMaxHoldTime()
    }

    override fun getRepetitionCount(): Int {
        return homeExercise.getRepetitionCount()
    }
}
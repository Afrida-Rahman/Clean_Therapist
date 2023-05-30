package org.mmh.clean_therapist.android.feature_exercise.domain.repository

import android.content.Context
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase

interface ExerciseRepository {

    fun initExercise(context: Context, exerciseId: Int, active: Boolean)

    fun setImageOrientation(imageFlipped: Boolean)

    fun setExerciseDetails(
        exerciseName: String,
        exerciseInstruction: String?,
        exerciseImageUrls: List<String>,
        exerciseVideoUrls: String,
        repetitionLimit: Int,
        setLimit: Int,
        protoId: Int,
    )

    fun setConsideredIndices(phases: List<Phase>)

    fun getMaxHoldTime(): Int

    fun getRepetitionCount(): Int

}
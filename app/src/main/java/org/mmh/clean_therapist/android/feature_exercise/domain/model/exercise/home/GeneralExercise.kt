package org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home

import android.content.Context

class GeneralExercise(
    context: Context,
    exerciseId: Int,
    active: Boolean = false
) : HomeExercise(
    context = context,
    id = exerciseId,
    active = active
)
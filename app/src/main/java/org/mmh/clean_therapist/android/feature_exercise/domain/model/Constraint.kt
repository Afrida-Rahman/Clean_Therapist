package org.mmh.clean_therapist.android.feature_exercise.domain.model

import org.mmh.clean_therapist.android.core.util.Draw


interface Constraint {
    fun draw(draw: Draw)
}
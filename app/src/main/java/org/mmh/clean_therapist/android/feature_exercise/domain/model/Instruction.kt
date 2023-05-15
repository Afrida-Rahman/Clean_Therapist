package org.mmh.clean_therapist.android.feature_exercise.domain.model

import android.media.MediaPlayer

data class Instruction(
    val text: String,
    val player: MediaPlayer?
)

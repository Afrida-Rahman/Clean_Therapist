package org.mmh.clean_therapist.android.feature_bot.presentation.utils

import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.feature_bot.domain.model.Bot
import org.mmh.clean_therapist.android.ui.theme.Blue900
import org.mmh.clean_therapist.android.ui.theme.Green
import org.mmh.clean_therapist.android.ui.theme.Yellow

object BotUtils {
    fun getBots(): List<Bot> = listOf(
        Bot(
            name = "Postural Assessment",
            codeName = "POSTURE_BOT",
            icon = R.drawable.posture_screening,
            backgroundColor = Blue900
        ),
        Bot(
            name = "Fysical Score™ Assessment",
            codeName = "MSK_BOT",
            icon = R.drawable.user,
            backgroundColor = Green
        ),
        Bot(
            name = "Posture or Fysical Score™ Results",
            codeName = "PAIN",
            icon = R.drawable.fysical_score,
            backgroundColor = Yellow
        )
    )

    fun getBot(codeName: String): Bot = getBots().find { it.codeName == codeName } ?: getBots()[0]
}
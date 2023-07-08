package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.Exercise

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mmh.clean_therapist.android.core.util.AsyncAudioPlayer
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Instruction
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase

class Instructions {
    private lateinit var instructions: MutableList<Instruction>
    private lateinit var asyncAudioPlayer: AsyncAudioPlayer
    var takingRest = false
    var manuallyPaused = false
    private var previousCountDown = 0

    private val commonExerciseInstructions = listOf(
        AsyncAudioPlayer.GET_READY,
        AsyncAudioPlayer.START,
        AsyncAudioPlayer.START_AGAIN,
        AsyncAudioPlayer.FINISH,
        AsyncAudioPlayer.ONE,
        AsyncAudioPlayer.TWO,
        AsyncAudioPlayer.THREE,
        AsyncAudioPlayer.FOUR,
        AsyncAudioPlayer.FIVE,
        AsyncAudioPlayer.SIX,
        AsyncAudioPlayer.SEVEN,
        AsyncAudioPlayer.EIGHT,
        AsyncAudioPlayer.NINE,
        AsyncAudioPlayer.TEN,
        AsyncAudioPlayer.ELEVEN,
        AsyncAudioPlayer.TWELVE,
        AsyncAudioPlayer.THIRTEEN,
        AsyncAudioPlayer.FOURTEEN,
        AsyncAudioPlayer.FIFTEEN,
        AsyncAudioPlayer.SIXTEEN,
        AsyncAudioPlayer.SEVENTEEN,
        AsyncAudioPlayer.EIGHTEEN,
        AsyncAudioPlayer.NINETEEN,
        AsyncAudioPlayer.TWENTY,
        AsyncAudioPlayer.BEEP,
        AsyncAudioPlayer.PAUSE,
    )

    fun init(context: Context, phases: List<Phase>) {
        asyncAudioPlayer = AsyncAudioPlayer(context)
        commonExerciseInstructions.forEach {
            addInstruction(it)
        }
        for (phase in phases) {
            addInstruction(phase.instruction)
        }
    }

    private fun addInstruction(dialogue: String?) {
        dialogue?.let { text ->
            val doesNotExist = instructions.find {
                it.text.lowercase() == text.lowercase()
            } == null
            if (doesNotExist) {
                instructions.add(asyncAudioPlayer.generateInstruction(dialogue))
            }
        }
    }

    fun playTextInstruction(instruction: Instruction){
        asyncAudioPlayer.playText(instruction)
    }

    fun getInstruction(text: String): Instruction {
        var instruction = instructions.find {
            it.text.lowercase() == text.lowercase()
        }
        if (instruction == null) {
            instruction = asyncAudioPlayer.generateInstruction(text)
            instructions.add(instruction)
        }
        return instruction
    }

    fun playInstruction(
        firstDelay: Long,
        firstInstruction: String,
        secondDelay: Long = 0L,
        secondInstruction: String? = null,
        shouldTakeRest: Boolean = false
    ) {
        if (shouldTakeRest) takingRest = true
        CoroutineScope(Dispatchers.Main).launch {
            val instruction = getInstruction(firstInstruction)
            delay(firstDelay)
            playTextInstruction(instruction)
            delay(secondDelay)
            secondInstruction?.let {
                playTextInstruction(getInstruction(it))
            }
            if (shouldTakeRest and !manuallyPaused) takingRest = false
        }
    }

    fun countDownAudio(count: Int) {
        if (previousCountDown != count && count > 0) {
            previousCountDown = count
            if (count > 20) {
                asyncAudioPlayer.playText(getInstruction(AsyncAudioPlayer.BEEP))
            } else {
                asyncAudioPlayer.playText(getInstruction(count.toString()))
            }
        }
    }
}
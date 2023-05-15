package org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home

import android.content.Context
import androidx.annotation.RawRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.util.AsyncAudioPlayer
import org.mmh.clean_therapist.android.core.util.AudioPlayer
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.domain.model.*
import org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint.AngleConstraint
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.VisualUtils
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.VisualUtils.getIndexName
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

abstract class HomeExercise(
    val context: Context,
    val id: Int,
    val playPauseCue: Boolean = true,
    var name: String = "",
    val active: Boolean = true,
    var protocolId: Int = 0,
    var instruction: String? = "",
    var videoUrls: String = "",
    var imageUrls: List<String> = listOf(),
    var maxRepCount: Int = 0,
    var maxSetCount: Int = 0,
) {

    companion object {
        private const val SET_INTERVAL = 7000L
        private const val MAX_DISTANCE_FROM_CAMERA = 13
    }

    open var phaseIndex = 0
    open var rightCountPhases = mutableListOf<Phase>()
    private var phaseSummary = mutableListOf<PhaseSummary>()
    private var restriction = mutableListOf<Restriction>()
    private val audioPlayer = AudioPlayer(context)
    private var setCounter = 0
    private var wrongCounter = 0
    private var repetitionCounter = 0
    private var lastTimePlayed: Int = System.currentTimeMillis().toInt()
    private var focalLengths: FloatArray? = null
    private var previousCountDown = 0
    private var downTimeCounter = 0
    private var distanceFromCamera = 0f
    private var lastTimeDistanceCalculated = 0L
    private val distanceCalculationInterval = 2000L
    open var phaseEntered = false
    private var phaseEnterTime = System.currentTimeMillis()
    private var takingRest = false
    private var manuallyPaused = false
    private lateinit var asyncAudioPlayer: AsyncAudioPlayer
    fun isAsyncAudioPlayerInitialized() = ::asyncAudioPlayer.isInitialized
    val instructions: MutableList<Instruction> = mutableListOf()
    var trackIndex: Int = 0
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

    private val consideredIndices = mutableSetOf<Int>()

    fun addInstruction(dialogue: String?) {
        dialogue?.let { text ->
            val doesNotExist = instructions.find {
                it.text.lowercase() == text.lowercase()
            } == null
            if (doesNotExist) {
                instructions.add(asyncAudioPlayer.generateInstruction(dialogue))
            }
        }
    }

    fun setExercise(
        exerciseName: String,
        exerciseInstruction: String?,
        exerciseImageUrls: List<String>,
        exerciseVideoUrls: String,
        repetitionLimit: Int,
        setLimit: Int,
        protoId: Int,
    ) {
        name = exerciseName
        maxRepCount = repetitionLimit
        maxSetCount = setLimit
        protocolId = protoId
        instruction = exerciseInstruction
        imageUrls = exerciseImageUrls
        videoUrls = exerciseVideoUrls
    }

    fun setConsideredIndices(phases: List<Phase>) {
        playInstruction(
            firstDelay = 5000L,
            firstInstruction = AsyncAudioPlayer.GET_READY,
            secondDelay = 5000L,
            secondInstruction = AsyncAudioPlayer.START,
            shouldTakeRest = true
        )
        asyncAudioPlayer = AsyncAudioPlayer(context)
        commonExerciseInstructions.forEach {
            addInstruction(it)
        }
        for (phase in phases) {
            for (constraint in phase.constraints) {
                consideredIndices.add(constraint.startPointIndex)
                consideredIndices.add(constraint.middlePointIndex)
                consideredIndices.add(constraint.endPointIndex)
            }
            addInstruction(phase.instruction)
        }
    }

    fun getMaxHoldTime(): Int = rightCountPhases.map { it.holdTime }.maxOrNull() ?: 0

    fun getRepetitionCount() = repetitionCounter

    fun getWrongCount() = wrongCounter

    fun getSetCount() = setCounter

    fun getHoldTimeLimitCount(): Int = downTimeCounter

    fun getPhase(): Phase? {
        return if (phaseIndex < rightCountPhases.size) {
            rightCountPhases[phaseIndex]
        } else {
            null
        }
    }

    fun getPhaseSummary(): List<PhaseSummary> = phaseSummary

    fun pauseExercise() {
        takingRest = true
        manuallyPaused = true
    }

    fun resumeExercise() {
        takingRest = false
        manuallyPaused = false
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
            asyncAudioPlayer.playText(instruction)
            delay(secondDelay)
            secondInstruction?.let {
                asyncAudioPlayer.playText(getInstruction(it))
            }
            if (shouldTakeRest and !manuallyPaused) takingRest = false
        }
    }

    fun getPersonDistance(person: Person): Float {
        return if (System.currentTimeMillis() >= lastTimeDistanceCalculated + distanceCalculationInterval) {
            val pointA = person.keyPoints[BodyPart.LEFT_SHOULDER.position]
            val pointB = person.keyPoints[BodyPart.LEFT_ELBOW.position]
            val distanceInPx = sqrt(
                (pointA.coordinate.x - pointB.coordinate.x).toDouble()
                    .pow(2) + (pointA.coordinate.y - pointB.coordinate.y).toDouble().pow(2)
            )
            var sum = 0f
            var distance: Float? = null
            focalLengths?.let {
                focalLengths?.forEach { value ->
                    sum += value
                }
                val avgFocalLength = (sum / focalLengths!!.size) * 0.04f
                distance = (avgFocalLength / distanceInPx.toFloat()) * 12 * 3000f
            }
            distanceFromCamera = distance?.let { it / 12 } ?: 4f
            distanceFromCamera
        } else {
            distanceFromCamera
        }
    }

    fun setFocalLength(lengths: FloatArray?) {
        focalLengths = lengths
    }

    fun playAudio(@RawRes resource: Int) {
        val timestamp = System.currentTimeMillis().toInt()
        if (timestamp - lastTimePlayed >= 3500) {
            lastTimePlayed = timestamp
            audioPlayer.playFromFile(resource)
        }
    }

    open fun onEvent(event: CommonInstructionEvent) {
        when (event) {
            is CommonInstructionEvent.OutSideOfBox -> {

                playInstruction(
                    firstDelay = 0L,
                    firstInstruction = AsyncAudioPlayer.PLEASE_STAY_INSIDE_BOX
                )
            }
            is CommonInstructionEvent.HandIsNotStraight -> playAudio(R.raw.keep_hand_straight)
            is CommonInstructionEvent.LeftHandIsNotStraight -> playAudio(R.raw.left_hand_straight)
            is CommonInstructionEvent.RightHandIsNotStraight -> playAudio(R.raw.right_hand_straight)
            is CommonInstructionEvent.TooFarFromCamera -> playAudio(R.raw.come_forward)
        }
    }

    open fun rightExerciseCount(
        person: Person,
        canvasHeight: Int,
        canvasWidth: Int
    ) {
        if (phaseIndex < rightCountPhases.size) {
            val phase = rightCountPhases[phaseIndex]
            val minConfidenceSatisfied = isMinConfidenceSatisfied(phase, person)
            if (rightCountPhases.isNotEmpty() && minConfidenceSatisfied && !takingRest) {

                val constraintSatisfied = isConstraintSatisfied(
                    person,
                    phase.constraints
                )
                if (setCounter == 0) {
                    trackMinMaxConstraints(person = person)
                }
                if (VisualUtils.isInsideBox(
                        person,
                        consideredIndices.toList(),
                        canvasHeight,
                        canvasWidth
                    ) && constraintSatisfied
                ) {
                    if (!phaseEntered) {
                        phaseEntered = true
                        phaseEnterTime = System.currentTimeMillis()
                    }
                    val elapsedTime = ((System.currentTimeMillis() - phaseEnterTime) / 1000).toInt()
                    downTimeCounter = phase.holdTime - elapsedTime
                    if (downTimeCounter <= 0) {
                        if (phaseIndex == rightCountPhases.size - 1) {
                            phaseIndex = 0
                            repetitionCount()
                        } else {
                            phaseIndex++
                            rightCountPhases[phaseIndex].instruction?.let {
                                playInstruction(firstDelay = 500L, firstInstruction = it)
                            }
                            downTimeCounter = 0
                        }
                    } else {
                        if (phaseIndex != 0) countDownAudio(downTimeCounter)
                    }
                } else {
                    downTimeCounter = 0
                    phaseEntered = false
                }
                commonInstruction(
                    person,
                    rightCountPhases[phaseIndex].constraints,
                    canvasHeight,
                    canvasWidth
                )
                exerciseInstruction(person)
            }
        }
    }

    open fun wrongExerciseCount(person: Person, canvasHeight: Int, canvasWidth: Int) {}

    open fun exerciseInstruction(person: Person) {}

    private fun isMinConfidenceSatisfied(phase: Phase, person: Person): Boolean {
        val indices = mutableSetOf<Int>()
        var isSatisfied = true
        phase.constraints.forEach {
            if(it is AngleConstraint) {
                val angleConstraint = it as AngleConstraint
                indices.add(angleConstraint.startPointIndex)
                indices.add(angleConstraint.middlePointIndex)
                indices.add(angleConstraint.endPointIndex)
            }
        }
        for (index in 0 until person.keyPoints.size) {
            if (person.keyPoints[index].bodyPart.position in indices && person.keyPoints[index].score < 0.3f) {
                isSatisfied = false
                break
            }
        }
        return isSatisfied
    }

    private fun getInstruction(text: String): Instruction {
        var instruction = instructions.find {
            it.text.lowercase() == text.lowercase()
        }
        if (instruction == null) {
            instruction = asyncAudioPlayer.generateInstruction(text)
            instructions.add(instruction)
        }
        return instruction
    }

    private fun repetitionCount() {
        repetitionCounter++
        if (repetitionCounter >= maxRepCount) {
            repetitionCounter = 0
            setCounter++
            if (setCounter == maxSetCount) {
                asyncAudioPlayer.playText(getInstruction(AsyncAudioPlayer.FINISH))
                CoroutineScope(Dispatchers.Main).launch {
                    playInstruction(
                        firstDelay = 1000L,
                        firstInstruction = AsyncAudioPlayer.CONGRATS
                    )
                }
            } else {
                playInstruction(
                    firstDelay = 0L,
                    firstInstruction = setCountText(setCounter),
                    secondDelay = SET_INTERVAL,
                    secondInstruction = AsyncAudioPlayer.START_AGAIN,
                    shouldTakeRest = true
                )
            }
            if (setCounter == 1) {
                setNewConstraints()
            }
        } else {
            val phase = rightCountPhases[0]
            if (phase.holdTime > 0) {
                val repetitionInstruction = getInstruction(repetitionCounter.toString())
                playInstruction(
                    firstDelay = 0L,
                    firstInstruction = repetitionCounter.toString(),
                    secondDelay = repetitionInstruction.player?.duration?.toLong() ?: 500L,
                    secondInstruction = if (playPauseCue) AsyncAudioPlayer.PAUSE else null,
                    shouldTakeRest = true
                )
            } else {
                playInstruction(
                    firstDelay = 0L,
                    firstInstruction = repetitionCounter.toString()
                )
            }
        }
    }

    fun setNewConstraints() {
        trackIndex = 0
        rightCountPhases.forEach { phase ->
            phase.constraints.forEach { constraint ->
                val standardValues = constraint.getStandardConstraints()
                val standardConstraintGap = standardValues.standardMax - standardValues.standardMin
                val minMaxMedian = constraint.getMinMaxMedian()
                val refinedConstraintGap = minMaxMedian.max - minMaxMedian.max

                if (minMaxMedian.median < standardValues.standardMin || minMaxMedian.median > standardValues.standardMax) {
                    if (refinedConstraintGap > standardConstraintGap) {
                        val newMin = minMaxMedian.median - (standardConstraintGap / 2)
                        val newMax = minMaxMedian.median + (standardConstraintGap / 2)
                        constraint.setRefinedConstraints(min = newMin, max = newMax)
                    } else {
                        val minimumGap = 15
                        if (refinedConstraintGap <= minimumGap) {
                            val newMin = minMaxMedian.median - (minimumGap / 2)
                            val newMax = minMaxMedian.median + (minimumGap / 2)
                            constraint.setRefinedConstraints(min = newMin, max = newMax)
                        } else {
                            val newMin = minMaxMedian.median - (refinedConstraintGap / 2)
                            val newMax = minMaxMedian.median + (refinedConstraintGap / 2)
                            constraint.setRefinedConstraints(min = newMin, max = newMax)
                        }
                    }

                } else {
                    constraint.setStandardConstraints()
                }
                constraint.storedValues.clear()

                restriction.add(
                    Restriction(
                        StartKeyPosition = getIndexName(constraint.startPointIndex),
                        MiddleKeyPosition = getIndexName(constraint.middlePointIndex),
                        EndKeyPosition = getIndexName(constraint.endPointIndex),
                        AverageMax = constraint.lowestMinValidationValue,
                        AverageMin = constraint.lowestMaxValidationValue
                    )
                )

                phaseSummary.add(
                    PhaseSummary(
                        PhaseNumber = phase.id,
                        Restrictions = restriction.toMutableList()
                    )
                )
                restriction.clear()

            }

        }

    }

    private fun setCountText(count: Int): String = when (count) {
        1 -> AsyncAudioPlayer.SET_1
        2 -> AsyncAudioPlayer.SET_2
        3 -> AsyncAudioPlayer.SET_3
        4 -> AsyncAudioPlayer.SET_4
        5 -> AsyncAudioPlayer.SET_5
        6 -> AsyncAudioPlayer.SET_6
        7 -> AsyncAudioPlayer.SET_7
        8 -> AsyncAudioPlayer.SET_8
        9 -> AsyncAudioPlayer.SET_9
        10 -> AsyncAudioPlayer.SET_10
        else -> AsyncAudioPlayer.SET_COMPLETED
    }

    private fun isConstraintSatisfied(person: Person, constraints: List<Constraint>): Boolean {
        var constraintSatisfied = true
        constraints.forEach {
            if(it is AngleConstraint) {
                val angle = Utilities.angle(
                    startPoint = person.keyPoints[it.startPointIndex].toRealPoint(),
                    middlePoint = person.keyPoints[it.middlePointIndex].toRealPoint(),
                    endPoint = person.keyPoints[it.endPointIndex].toRealPoint(),
                    clockWise = !it.isClockwise
                )
                val minValue = min(it.lowestMinValidationValue, it.minValidationValue)
                val maxValue = max(it.lowestMaxValidationValue, it.maxValidationValue)
                if (angle < minValue.toFloat() || angle > maxValue.toFloat()) {
                    constraintSatisfied = false
                }
            }

        }
        return constraintSatisfied
    }

    private fun getCurrentPhase(person: Person, phases: List<Phase>): Int {
        val reversedPhases = phases.sortedBy { it.id }.reversed()
        var index = phases.size - 2
        while (index >= 0) {
            if (isConstraintSatisfied(person, reversedPhases[index].constraints)) {
                return index
            }
            index--
        }
        return -1
    }

    fun sortedPhaseList(phases: List<Phase>): List<Phase> {
        val phaseIndices = mutableListOf<Int>()
        return phases.sortedBy { it.id }.filter {
            val shouldAdd = !phaseIndices.contains(it.id)
            phaseIndices.add(it.id)
            shouldAdd
        }
    }

    private fun commonInstruction(
        person: Person,
        constraints: List<Constraint>,
        canvasHeight: Int,
        canvasWidth: Int
    ) {
        constraints.forEach { _ ->
            if (!VisualUtils.isInsideBox(
                    person,
                    consideredIndices.toList(),
                    canvasHeight,
                    canvasWidth
                )
            ) onEvent(CommonInstructionEvent.OutSideOfBox)
        }
        if (getPersonDistance(person) > MAX_DISTANCE_FROM_CAMERA) {
//            onEvent(CommonInstructionEvent.TooFarFromCamera)
        }
    }

    private fun countDownAudio(count: Int) {
        if (previousCountDown != count && count > 0) {
            previousCountDown = count
            if (count > 20) {
                asyncAudioPlayer.playText(getInstruction(AsyncAudioPlayer.BEEP))
            } else {
                asyncAudioPlayer.playText(getInstruction(count.toString()))
            }
        }
    }

    sealed class CommonInstructionEvent {
        object OutSideOfBox : CommonInstructionEvent()
        object HandIsNotStraight : CommonInstructionEvent()
        object LeftHandIsNotStraight : CommonInstructionEvent()
        object RightHandIsNotStraight : CommonInstructionEvent()
        object TooFarFromCamera : CommonInstructionEvent()
    }

    private fun trackMinMaxConstraints(person: Person) {
        rightCountPhases[trackIndex].constraints.forEach {
            if(it is AngleConstraint) {
                val angle = Utilities.angle(
                    startPoint = person.keyPoints[it.startPointIndex].toRealPoint(),
                    middlePoint = person.keyPoints[it.middlePointIndex].toRealPoint(),
                    endPoint = person.keyPoints[it.endPointIndex].toRealPoint(),
                    clockWise = it.isClockwise
                )
                if (angle < it.lowestMinValidationValue || angle > it.lowestMaxValidationValue) {
                    if (phaseIndex != trackIndex) {
                        trackIndex = phaseIndex
                    }
                } else {
                    it.storedValues.add(angle.toInt())
                }
            }
        }
    }
}

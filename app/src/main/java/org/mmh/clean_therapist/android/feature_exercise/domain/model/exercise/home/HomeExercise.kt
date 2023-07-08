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
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.Exercise.Instructions
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

abstract class HomeExercise(
    val context: Context,
    val id: Int,
    val playPauseCue: Boolean = true,
    var name: String = "",
    private var protocolId: Int = 0,
    var instruction: String? = "",
    private var videoUrls: String = "",
    private var imageUrls: List<String> = listOf(),
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
    private var downTimeCounter = 0
    private var distanceFromCamera = 0f
    private var lastTimeDistanceCalculated = 0L
    private val distanceCalculationInterval = 2000L
    open var phaseEntered = false
    private var phaseEnterTime = System.currentTimeMillis()
    private var takingRest = false
    private var manuallyPaused = false
    private var trackIndex = 0
    private var instructions = Instructions()

    private val consideredIndices = mutableSetOf<Int>()

    private var isImageFlipped: Boolean = false
    fun setImageFlipped(imageFlipped: Boolean) {
        isImageFlipped = imageFlipped
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
        instructions.playInstruction(
            firstDelay = 5000L,
            firstInstruction = AsyncAudioPlayer.GET_READY,
            secondDelay = 5000L,
            secondInstruction = AsyncAudioPlayer.START,
            shouldTakeRest = true
        )
        instructions.init(context, phases)
        for (phase in phases) {
            for (constraint in phase.constraints) {
                consideredIndices.add(constraint.startPointIndex)
                consideredIndices.add(constraint.middlePointIndex)
                consideredIndices.add(constraint.endPointIndex)
            }
        }
    }

    fun getMaxHoldTime(): Int = rightCountPhases.maxOfOrNull { it.holdTime } ?: 0

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

    fun pauseExercise() {
        takingRest = true
        manuallyPaused = true
    }

    fun resumeExercise() {
        takingRest = false
        manuallyPaused = false
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

    private fun playAudio(@RawRes resource: Int) {
        val timestamp = System.currentTimeMillis().toInt()
        if (timestamp - lastTimePlayed >= 3500) {
            lastTimePlayed = timestamp
            audioPlayer.playFromFile(resource)
        }
    }

    open fun onEvent(event: CommonInstructionEvent) {
        when (event) {
            is CommonInstructionEvent.OutSideOfBox -> {

                instructions.playInstruction(
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
                                instructions.playInstruction(firstDelay = 500L, firstInstruction = it)
                            }
                            downTimeCounter = 0
                        }
                    } else {
                        if (phaseIndex != 0) instructions.countDownAudio(downTimeCounter)
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


    open fun exerciseInstruction(person: Person) {}

    private fun isMinConfidenceSatisfied(phase: Phase, person: Person): Boolean {
        val indices = mutableSetOf<Int>()
        var isSatisfied = true
        phase.constraints.forEach {
            if (it is AngleConstraint) {
                val angleConstraint = it
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

    private fun repetitionCount() {
        repetitionCounter++
        if (repetitionCounter >= maxRepCount) {
            repetitionCounter = 0
            setCounter++
            if (setCounter == maxSetCount) {
                instructions.playTextInstruction(instructions.getInstruction(AsyncAudioPlayer.FINISH))
                CoroutineScope(Dispatchers.Main).launch {
                    instructions.playInstruction(
                        firstDelay = 1000L,
                        firstInstruction = AsyncAudioPlayer.CONGRATS
                    )
                }
            } else {
                instructions.playInstruction(
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
                val repetitionInstruction = instructions.getInstruction(repetitionCounter.toString())
                instructions.playInstruction(
                    firstDelay = 0L,
                    firstInstruction = repetitionCounter.toString(),
                    secondDelay = repetitionInstruction.player?.duration?.toLong() ?: 500L,
                    secondInstruction = if (playPauseCue) AsyncAudioPlayer.PAUSE else null,
                    shouldTakeRest = true
                )
            } else {
                instructions.playInstruction(
                    firstDelay = 0L,
                    firstInstruction = repetitionCounter.toString()
                )
            }
        }
    }

    private fun setNewConstraints() {
        trackIndex = 0
        rightCountPhases.forEach { phase ->
            phase.constraints.forEach { constraint ->
                val standardValues = constraint.getStandardConstraints()
                val standardConstraintGap = standardValues.standardMax - standardValues.standardMin
                val minMaxMedian = constraint.getMinMaxMedian()
                val refinedConstraintGap = minMaxMedian.max - minMaxMedian.min
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
            if (it is AngleConstraint) {
                var direction: Boolean = it.isClockwise
                if (isImageFlipped) {
                    direction = !it.isClockwise
                }
                val angle = Utilities.angle(
                    startPoint = person.keyPoints[it.startPointIndex].toRealPoint(),
                    middlePoint = person.keyPoints[it.middlePointIndex].toRealPoint(),
                    endPoint = person.keyPoints[it.endPointIndex].toRealPoint(),
                    clockWise = direction
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
            onEvent(CommonInstructionEvent.TooFarFromCamera)
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
            if (it is AngleConstraint) {
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

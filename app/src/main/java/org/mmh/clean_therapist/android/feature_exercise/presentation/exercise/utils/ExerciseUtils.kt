package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils

import android.util.Log
import org.mmh.clean_therapist.android.core.util.AsyncAudioPlayer
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Constraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.model.PhaseSummary
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Restriction
import org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint.AngleConstraint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.HomeExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.VisualUtils
import kotlin.math.max
import kotlin.math.min

object ExerciseUtils {
    open var rightCountPhases = mutableListOf<Phase>()
    private var phaseSummary = mutableListOf<PhaseSummary>()
    private var restriction = mutableListOf<Restriction>()
    private var setCounter = 0
    var repetitionCounter = 0
    private var downTimeCounter = 0
    open var phaseEntered = false
    private var phaseEnterTime = System.currentTimeMillis()
    private var takingRest = false
    var trackIndex: Int = 0
    private var phaseIndex = 0
    private lateinit var asyncAudioPlayer: AsyncAudioPlayer
    private var previousCountDown = 0
    private var lastTimePlayed: Int = System.currentTimeMillis().toInt()
    private var manuallyPaused = false
    private val consideredIndices = mutableSetOf<Int>()
    lateinit var homeExercise: HomeExercise

    fun rightCount(
        person: Person,
        canvasHeight: Int,
        canvasWidth: Int,
        rightCountPhases: List<Phase>,
        isImageFLipped: Boolean
    ) {
        if (phaseIndex < rightCountPhases.size) {
            val phase = rightCountPhases[phaseIndex]
            val minConfidenceSatisfied = isMinConfidenceSatisfied(phase, person)
            if (rightCountPhases.isNotEmpty() && minConfidenceSatisfied && !takingRest) {

                val constraintSatisfied = isConstraintSatisfied(
                    person,
                    phase.constraints,
                    isImageFLipped
                )
//                if (setCounter == 0) {
//                    trackMinMaxConstraints(person = person)
//                }
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
                        println("Phase-> down time counter : $downTimeCounter")
                        if (phaseIndex == rightCountPhases.size - 1) {
                            phaseIndex = 0
                            repetitionCount()
                        } else {
                            phaseIndex++
                            println("Phase-> index : $phaseIndex")
                            rightCountPhases[phaseIndex].instruction?.let {
                                println("Phase-> instruction : $it")
//                                playInstruction(firstDelay = 500L, firstInstruction = it)
                            }
                            downTimeCounter = 0
                        }
                    } else {
                        if (phaseIndex != 0) {
                            println("Phase-> index not equal to zero!")
//                            countDownAudio(downTimeCounter)
                        }
                    }
                } else {
                    downTimeCounter = 0
                    phaseEntered = false
                }
//                commonInstruction(
//                    person,
//                    rightCountPhases[phaseIndex].constraints,
//                    canvasHeight,
//                    canvasWidth
//                )
//                exerciseInstruction(person)
            }
        }
    }

    private fun isMinConfidenceSatisfied(phase: Phase, person: Person): Boolean {
        val indices = mutableSetOf<Int>()
        var isSatisfied = true
        phase.constraints.forEach {
            if (it is AngleConstraint) {
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

    private fun isConstraintSatisfied(
        person: Person,
        constraints: List<Constraint>,
        isImageFlipped: Boolean
    ): Boolean {
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

//                println("min Value: ${minValue}   angle: $angle   max value: $maxValue")
                if (angle < minValue.toFloat() || angle > maxValue.toFloat()) {
                    constraintSatisfied = false
                }
            }

        }
        return constraintSatisfied
    }

    fun getRepetitionCount() = repetitionCounter
    fun getSetCount() = setCounter

    private fun repetitionCount() {
        repetitionCounter++
        Log.d("Phase-> exerciseRep", "Rep count : $repetitionCounter")
        if (repetitionCounter >= 3) {
            repetitionCounter = 0
            setCounter++
            if (setCounter == 2) {
                Log.d("Phase-> exerciseRep", "exercise completed the sets")
            } else {
                Log.d("Phase-> exerciseRep", "exercise not completed else block")
//                playInstruction(
//                    firstDelay = 0L,
//                    firstInstruction = setCountText(setCounter),
//                    secondDelay = HomeExercise.SET_INTERVAL,
//                    secondInstruction = AsyncAudioPlayer.START_AGAIN,
//                    shouldTakeRest = true
//                )
            }
            if (setCounter == 1) {
                setNewConstraints()
            }
        } else {
//            val phase = rightCountPhases[0]
//            if (phase.holdTime > 0) {
//                val repetitionInstruction = getInstruction(repetitionCounter.toString())
//                playInstruction(
//                    firstDelay = 0L,
//                    firstInstruction = repetitionCounter.toString(),
//                    secondDelay = repetitionInstruction.player?.duration?.toLong() ?: 500L,
//                    secondInstruction = if (playPauseCue) AsyncAudioPlayer.PAUSE else null,
//                    shouldTakeRest = true
//                )
//            } else {
//                playInstruction(
//                    firstDelay = 0L,
//                    firstInstruction = repetitionCounter.toString()
//                )
//            }
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
                        StartKeyPosition = VisualUtils.getIndexName(constraint.startPointIndex),
                        MiddleKeyPosition = VisualUtils.getIndexName(constraint.middlePointIndex),
                        EndKeyPosition = VisualUtils.getIndexName(constraint.endPointIndex),
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
}
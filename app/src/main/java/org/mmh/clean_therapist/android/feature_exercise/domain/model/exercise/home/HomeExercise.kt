package org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home

import android.content.Context
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.domain.model.*
import org.mmh.clean_therapist.android.feature_exercise.domain.model.constraint.AngleConstraint
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.VisualUtils
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.VisualUtils.getIndexName
import kotlin.math.max
import kotlin.math.min

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
    var trackIndex: Int = 0

    val consideredIndices = mutableSetOf<Int>()

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

    fun getRepetitionCount() = repetitionCounter

    fun getWrongCount() = wrongCounter

    fun getSetCount() = setCounter

    fun setFocalLength(lengths: FloatArray?) {
        focalLengths = lengths
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
                            }
                            downTimeCounter = 0
                        }
                    } else {
//                        if (phaseIndex != 0) countDownAudio(downTimeCounter)
                    }
                } else {
                    downTimeCounter = 0
                    phaseEntered = false
                }
            }
        }
    }

    open fun wrongExerciseCount(person: Person, canvasHeight: Int, canvasWidth: Int) {
    }

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


    private fun repetitionCount() {
        repetitionCounter++
        if (repetitionCounter >= maxRepCount) {
            repetitionCounter = 0
            setCounter++
            if (setCounter == 1) {
                setNewConstraints()
            }
        } else {
            val phase = rightCountPhases[0]
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

    fun getMaxHoldTime(): Int = rightCountPhases.map { it.holdTime }.maxOrNull() ?: 0
}

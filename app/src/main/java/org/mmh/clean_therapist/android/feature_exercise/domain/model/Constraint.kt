package org.mmh.clean_therapist.android.feature_exercise.domain.model

import org.mmh.clean_therapist.android.core.util.Draw


interface Constraint {
    val startPointIndex: Int
    val middlePointIndex: Int
    val endPointIndex: Int
    val minValidationValue: Int
    val maxValidationValue: Int
    var lowestMinValidationValue: Int
    var lowestMaxValidationValue: Int
    var storedValues: ArrayList<Int>

    fun draw(draw: Draw, person: Person)

    fun getStandardConstraints(): standardValue {
        return standardValue(minValidationValue, maxValidationValue)
    }

    fun getMinMaxMedian(): trimmedValues {
        val trimmedStoredValues = trimStoredValues(storedValues = storedValues)
        //Log.d("looseConstraint", "trimmedStoredValues:: $trimmedStoredValues")
        if (trimmedStoredValues.isEmpty()) {
            return trimmedValues(0, 0, 0)
        }
        val median = calculateMedianValue(storedValues = trimmedStoredValues)
        val min = trimmedStoredValues[0]
        val max = trimmedStoredValues[trimmedStoredValues.count() - 1]

        return trimmedValues(min, max, median)
    }

    fun setRefinedConstraints(min: Int, max: Int) {
        lowestMinValidationValue = min
        lowestMaxValidationValue = max
    }

    fun calculateMedianValue(storedValues: IntArray): Int {
        val n = storedValues.count()
        var median = 0
        if (n == 1) {
            return storedValues[0]
        }
        median = if ((n % 2) != 0) {
            storedValues[n / 2]
        } else {
            (storedValues[(n - 1) / 2] + storedValues[(n + 1) / 2]) / 2
        }
        return median
    }

    fun trimStoredValues(storedValues: ArrayList<Int>): IntArray {
        val trimCount = 2
        val storedValueSorted = storedValues.sorted()
        if (storedValueSorted.count() > 10) {
            for (i in 0..trimCount) {
                storedValueSorted.toMutableList().apply { removeAt(0) }
                storedValueSorted.toMutableList().apply { storedValueSorted.count() - 1 }
            }
        }
        return storedValueSorted.toIntArray()
    }

    fun setStandardConstraints() {
        lowestMinValidationValue = minValidationValue
        lowestMaxValidationValue = maxValidationValue
    }

}

package org.mmh.clean_therapist.android.core.util

import android.content.SharedPreferences
import org.mmh.clean_therapist.android.core.model.Point
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import java.util.*
import kotlin.math.acos
import kotlin.math.sqrt

object Utilities {
    fun angle(
        startPoint: Point,
        middlePoint: Point = Point(0f, 0f),
        endPoint: Point = Point(1f, 0f),
        clockWise: Boolean = false
    ): Float {
        if ((middlePoint != Point(0f, 0f)) && (endPoint != Point(1f, 0f))) {
            val vectorBA = Point(startPoint.x - middlePoint.x, startPoint.y - middlePoint.y)
            val vectorBC = Point(endPoint.x - middlePoint.x, endPoint.y - middlePoint.y)
            val vectorBAAngle = angle(vectorBA)
            val vectorBCAngle = angle(vectorBC)
            var angleValue = if (vectorBAAngle > vectorBCAngle) {
                vectorBAAngle - vectorBCAngle
            } else {
                360 + vectorBAAngle - vectorBCAngle
            }
            if (clockWise) {
                angleValue = 360 - angleValue
            }
            return angleValue
        } else {
            val x = startPoint.x
            val y = startPoint.y
            val magnitude = sqrt((x * x + y * y).toDouble())
            var angleValue = if (magnitude >= 0.0001) {
                acos(x / magnitude)
            } else {
                0
            }
            angleValue = Math.toDegrees(angleValue.toDouble())
            if (y < 0) {
                angleValue = 360 - angleValue
            }
            return angleValue.toFloat()
        }
    }

    fun currentDate(): String {
        val currentDate = Calendar.getInstance()
        val day = currentDate.get(Calendar.DATE)
        val month = currentDate.get(Calendar.MONTH)
        val year = currentDate.get(Calendar.YEAR)
        return "$month/$day/$year"
    }

    fun getPatient(preferences: SharedPreferences): Patient {
        return Patient(
            firstName = preferences.getString(Patient.FIRST_NAME, "") ?: "",
            lastName = preferences.getString(Patient.LAST_NAME, "") ?: "",
            patientId = preferences.getString(Patient.PATIENT_ID, "") ?: "",
            tenant = preferences.getString(Patient.TENANT, "") ?: "emma",
            email = preferences.getString(Patient.EMAIL, "") ?: "",
            loggedIn = preferences.getBoolean(Patient.LOGGED_IN, false),
            walkThroughPageShown = preferences.getBoolean(Patient.WALK_THROUGH_SHOWN, false),
        )
    }

    fun savePatient(preferences: SharedPreferences, data: Patient) {
        preferences.edit().apply {
            putString(Patient.FIRST_NAME, data.firstName)
            putString(Patient.LAST_NAME, data.lastName)
            putString(Patient.PATIENT_ID, data.patientId)
            putString(Patient.TENANT, data.tenant)
            putString(Patient.EMAIL, data.email)
            putBoolean(Patient.LOGGED_IN, data.loggedIn)
            putBoolean(Patient.WALK_THROUGH_SHOWN, data.walkThroughPageShown)
            apply()
        }
    }
}
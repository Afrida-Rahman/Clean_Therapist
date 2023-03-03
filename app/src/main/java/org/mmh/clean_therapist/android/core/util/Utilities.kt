package org.mmh.clean_therapist.android.core.util

import android.content.SharedPreferences
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient

object Utilities {

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
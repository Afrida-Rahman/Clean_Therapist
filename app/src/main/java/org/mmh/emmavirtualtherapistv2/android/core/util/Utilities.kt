package org.mmh.emmavirtualtherapistv2.android.core.util

import android.content.SharedPreferences
import org.mmh.emmavirtualtherapistv2.android.feature_authentication.domain.model.Patient

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
}
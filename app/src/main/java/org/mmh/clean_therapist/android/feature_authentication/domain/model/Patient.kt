package org.mmh.clean_therapist.android.feature_authentication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Patient(
    @PrimaryKey val id: Int? = null,
    val tenant: String,
    val patientId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val loggedIn: Boolean = false,
    val walkThroughPageShown: Boolean = false
) {
    companion object {
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val PATIENT_ID = "patientId"
        const val TENANT = "tenant"
        const val EMAIL = "email"
        const val LOGGED_IN = "logged_in"
        const val WALK_THROUGH_SHOWN = "walk_through_shown"
    }
}

class InvalidPatientException(message: String) : Exception(message)

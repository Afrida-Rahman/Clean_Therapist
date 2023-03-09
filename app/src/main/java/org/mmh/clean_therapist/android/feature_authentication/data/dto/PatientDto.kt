package org.mmh.clean_therapist.android.feature_authentication.data.dto

import com.google.gson.annotations.SerializedName
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient

data class PatientDto(
    @SerializedName("Success")
    val success: Boolean,
    @SerializedName("ContactData")
    val contactData: ContactData
)

fun PatientDto.toPatient(tenant: String, email: String): Patient {
    return Patient(
        tenant = tenant,
        patientId = contactData.patientId,
        firstName = contactData.firstName,
        lastName = contactData.lastName,
        email = email,
        loggedIn = true,
        walkThroughPageShown = true
    )
}
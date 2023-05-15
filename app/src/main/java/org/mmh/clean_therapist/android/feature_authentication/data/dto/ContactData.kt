package org.mmh.clean_therapist.android.feature_authentication.data.dto

import com.google.gson.annotations.SerializedName

data class ContactData(
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("PatientId")
    val patientId: String
)
package org.mmh.clean_therapist.android.feature_authentication.domain.payload

import com.google.gson.annotations.SerializedName

data class PatientInformationPayload(
    @SerializedName("Email") val email: String,
    @SerializedName("Password") val password: String,
    @SerializedName("Tenant") val tenant: String
)
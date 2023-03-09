package org.mmh.clean_therapist.android.feature_authentication.domain.repository

import org.mmh.clean_therapist.android.feature_authentication.data.dto.PatientDto
import org.mmh.clean_therapist.android.feature_authentication.domain.payload.PatientInformationPayload
import retrofit2.http.Body
import retrofit2.http.POST

interface RemotePatientRepository {

    @POST("/api/Account/GetCrmContact")
    suspend fun patientInformation(@Body payload: PatientInformationPayload): PatientDto
}
package org.mmh.clean_therapist.android.feature_authentication.domain.repository

import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import kotlinx.coroutines.flow.Flow

interface LocalPatientRepository {

    fun getPatients(): Flow<List<Patient>>

    suspend fun getLoggedInPatient(): Patient?

    suspend fun insertPatient(patient: Patient)

    suspend fun deletePatient(patient: Patient)
}
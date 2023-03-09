package org.mmh.clean_therapist.android.feature_authentication.data.repository

import org.mmh.clean_therapist.android.feature_authentication.data.data_source.PatientDao
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import org.mmh.clean_therapist.android.feature_authentication.domain.repository.LocalPatientRepository
import kotlinx.coroutines.flow.Flow

class LocalPatientRepositoryImpl(
    private val dao: PatientDao
) : LocalPatientRepository {
    override fun getPatients(): Flow<List<Patient>> {
        return dao.getPatients()
    }

    override suspend fun getLoggedInPatient(): Patient? {
        return dao.getLoggedInPatient()
    }

    override suspend fun insertPatient(patient: Patient) {
        dao.insertPatient(patient)
    }

    override suspend fun deletePatient(patient: Patient) {
        dao.deletePatient(patient)
    }
}
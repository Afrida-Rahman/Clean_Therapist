package org.mmh.clean_therapist.android.feature_authentication.domain.usecase

import org.mmh.clean_therapist.android.feature_authentication.domain.model.InvalidPatientException
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import org.mmh.clean_therapist.android.feature_authentication.domain.repository.LocalPatientRepository

class InsertPatient(
    private val repositoryPatientRepository: LocalPatientRepository
) {

    @Throws(InvalidPatientException::class)
    suspend operator fun invoke(patient: Patient) {
        if (patient.firstName.isBlank()) {
            throw InvalidPatientException("Patient's first name cannot be empty")
        }
        if (patient.firstName.isBlank()) {
            throw InvalidPatientException("Patient's last name cannot be empty")
        }
        if (patient.patientId.isBlank()) {
            throw InvalidPatientException("Patient's ID cannot be empty")
        }
        if (patient.tenant.isBlank()) {
            throw InvalidPatientException("Patient's tenant cannot be empty")
        }
        repositoryPatientRepository.insertPatient(patient)
    }
}
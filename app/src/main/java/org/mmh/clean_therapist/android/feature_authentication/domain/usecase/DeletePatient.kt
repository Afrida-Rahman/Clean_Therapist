package org.mmh.clean_therapist.android.feature_authentication.domain.usecase

import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import org.mmh.clean_therapist.android.feature_authentication.domain.repository.LocalPatientRepository

class DeletePatient(
    private val repositoryPatientRepository: LocalPatientRepository
) {

    suspend operator fun invoke(patient: Patient) {
        repositoryPatientRepository.deletePatient(patient)
    }
}

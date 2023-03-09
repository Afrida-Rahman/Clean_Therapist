package org.mmh.clean_therapist.android.feature_authentication.domain.usecase

import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import org.mmh.clean_therapist.android.feature_authentication.domain.repository.LocalPatientRepository
import kotlinx.coroutines.flow.Flow

class GetPatients(
    private val repositoryPatientRepository: LocalPatientRepository
) {

    operator fun invoke(): Flow<List<Patient>> {
        return repositoryPatientRepository.getPatients()
    }
}

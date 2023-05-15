package org.mmh.clean_therapist.android.feature_authentication.domain.usecase

import org.mmh.clean_therapist.android.feature_authentication.domain.usecase.DeletePatient
import org.mmh.clean_therapist.android.feature_authentication.domain.usecase.GetPatients
import org.mmh.clean_therapist.android.feature_authentication.domain.usecase.InsertPatient
import org.mmh.clean_therapist.android.feature_authentication.domain.usecase.PatientInformation

data class PatientUseCases(
    val getLoggedInPatient: GetLoggedInPatient,
    val getPatients: GetPatients,
    val insertPatient: InsertPatient,
    val deletePatient: DeletePatient,
    val patientInformation: PatientInformation
)

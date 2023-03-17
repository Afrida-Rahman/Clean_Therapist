package org.mmh.clean_therapist.android.feature_exercise.presentation.guideline

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GuidelineViewModel @Inject constructor(
) : ViewModel() {

    private val _patient = mutableStateOf<Patient?>(null)
    val patient: State<Patient?> = _patient
}
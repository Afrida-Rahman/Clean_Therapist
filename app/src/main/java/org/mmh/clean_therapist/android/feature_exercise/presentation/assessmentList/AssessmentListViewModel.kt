package org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Assessment
import org.mmh.clean_therapist.android.feature_exercise.domain.usecase.ExerciseUseCases
import javax.inject.Inject

@HiltViewModel
class AssessmentListViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases,
    private val preferences: SharedPreferences,
) : ViewModel() {
    private var originalAssessmentList: List<Assessment> = emptyList()

    private val _assessments = mutableStateOf<List<Assessment>>(emptyList())
    val assessments: State<List<Assessment>> = _assessments

    private val _patient = Utilities.getPatient(preferences)
    var patient: Patient = _patient

    private val _isAssessmentLoading = mutableStateOf(false)
    val isAssessmentLoading: State<Boolean> = _isAssessmentLoading

    private val _showTryAgainButton = mutableStateOf(false)
    val showTryAgain: State<Boolean> = _showTryAgainButton

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        fetchAssessments(patient)
    }

    fun onEvent(event: AssessmentListEvent) {
        when (event) {
            is AssessmentListEvent.FetchAssessments -> {
                fetchAssessments(patient)
            }

            is AssessmentListEvent.ApplyAssessmentFilter -> {
                _assessments.value = getAssessments(
                    searchTerm = event.testId ?: ""
                )
            }

            is AssessmentListEvent.SignOut -> {
                Utilities.savePatient(
                    preferences = preferences,
                    data = Patient(
                        id = null,
                        tenant = "",
                        patientId = "",
                        firstName = "",
                        lastName = "",
                        email = "",
                        loggedIn = false
                    )
                )
            }
        }
    }

    private fun getAssessments(searchTerm: String = ""): List<Assessment> {
        var assessments: List<Assessment> = originalAssessmentList
        if (searchTerm.isNotEmpty()) {
            assessments =
                originalAssessmentList.filter { it.testId.contains(searchTerm, ignoreCase = true) }
        }
        return assessments
    }

    private fun fetchAssessments(patient: Patient) {
        viewModelScope.launch {
            exerciseUseCases.fetchAssessments(
                tenant = patient.tenant,
                patientId = patient.patientId
            ).onEach {
                when (it) {
                    is Resource.Error -> {
                        _showTryAgainButton.value = true
                        _isAssessmentLoading.value = false
                        _eventFlow.emit(
                            UIEvent.ShowSnackBar(
                                it.message
                                    ?: "Failed to load assessments_outlined! Please try again."
                            )
                        )
                    }

                    is Resource.Loading -> {
                        _isAssessmentLoading.value = true
                        _showTryAgainButton.value = false
                    }

                    is Resource.Success -> {
                        _showTryAgainButton.value = false
                        _isAssessmentLoading.value = false
                        originalAssessmentList = it.data ?: emptyList()
                        _assessments.value = originalAssessmentList
                    }
                }
            }.launchIn(this)
        }
    }
}
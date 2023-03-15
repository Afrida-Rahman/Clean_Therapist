package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.usecase.ExerciseUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases,
    preferences: SharedPreferences
) : ViewModel() {
    private val patient = Utilities.getPatient(preferences)

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: ExerciseListEvent) {
        when (event) {
            is ExerciseListEvent.SaveDataButtonClicked -> {
                patient.let {
                    saveExerciseData(
                        tenant = it.tenant,
                        testId = event.testId,
                        patientId = it.patientId,
                        exercise = event.exercise,
                        repetitionCount = event.repetitionCount,
                        setCount = event.setCount,
                        wrongCount = event.wrongCount
                    )
                }
            }
        }
    }

    private fun saveExerciseData(
        tenant: String,
        testId: String,
        patientId: String,
        exercise: Exercise,
        repetitionCount: Int,
        setCount: Int,
        wrongCount: Int
    ) {
        viewModelScope.launch {
            exerciseUseCases.saveExerciseData(
                exercise = exercise,
                testId = testId,
                patientId = patientId,
                noOfReps = repetitionCount,
                noOfSets = setCount,
                noOfWrongCount = wrongCount,
                tenant = tenant
            ).onEach {
                when (it) {
                    is Resource.Error -> {
                        _eventFlow.emit(
                            UIEvent.ShowToastMessage(
                                it.message ?: "Unknown error"
                            )
                        )
                    }
                    is Resource.Loading -> {
                        _eventFlow.emit(UIEvent.ShowToastMessage("Please wait"))
                    }
                    is Resource.Success -> {
                        it.data?.let { exerciseTrackingResponse ->
                            _eventFlow.emit(
                                UIEvent.ShowToastMessage(
                                    exerciseTrackingResponse.message
                                )
                            )
                        }
                    }
                }
            }.launchIn(this)
        }
    }
}

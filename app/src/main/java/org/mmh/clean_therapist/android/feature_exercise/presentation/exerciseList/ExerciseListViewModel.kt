package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.usecase.networkData.ExerciseUseCases
import javax.inject.Inject

@HiltViewModel
class ExerciseListViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases,
    preferences: SharedPreferences
) : ViewModel() {
    private val patient = Utilities.getPatient(preferences)
    private var originalExerciseList: List<Exercise> = emptyList()
    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val _exercises = mutableStateOf<List<Exercise>?>(null)
    val exercises: State<List<Exercise>?> = _exercises

    private val _isExerciseLoading = mutableStateOf(true)
    val isExerciseLoading: State<Boolean> = _isExerciseLoading

    private val _showTryAgainButton = mutableStateOf(false)
    val showTryAgain: State<Boolean> = _showTryAgainButton

    private var searchCoroutine: Job? = null
    fun onEvent(event: ExerciseListEvent) {
        when (event) {
            is ExerciseListEvent.FetchExercises -> fetchExercises(
                tenant = event.tenant,
                testId = event.testId
            )

            is ExerciseListEvent.ApplyExerciseFilter -> {
                _exercises.value = getExercises(
                    testId = event.testId, searchTerm = event.exerciseName
                )
            }

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

    fun getExercise(testId: String, exerciseId: Int): Exercise? {
        return getExercises(testId = testId).find { it.id == exerciseId }
    }

    fun loadExercises(tenant: String, testId: String) {
        if (getExercises(testId = testId).isEmpty()) {
            fetchExercises(testId = testId, tenant = tenant)
        } else {
            searchExercises(testId = testId)
        }
    }

    private fun getExercises(testId: String, searchTerm: String = ""): List<Exercise> {
        var exercises: List<Exercise> = originalExerciseList
//        originalAssessmentList.find { it.testId == testId }?.let {
//            exercises = it.exercises
//        }
        if (searchTerm.isNotEmpty()) {
            exercises =
                originalExerciseList.filter { it.name.contains(searchTerm, ignoreCase = true) }
        }
        return exercises.sortedBy { it.name }
    }

    private fun searchExercises(testId: String, searchTerm: String = "") {
        searchCoroutine?.cancel()
        searchCoroutine = viewModelScope.launch {
            delay(500L)

            _exercises.value =
                getExercises(testId = testId, searchTerm = searchTerm)
        }
    }

    private fun fetchExercises(tenant: String, testId: String) {
        viewModelScope.launch {
            exerciseUseCases.fetchExercises(testId = testId, tenant = tenant)
                .onEach {
                    when (it) {
                        is Resource.Error -> {
                            _isExerciseLoading.value = false
                            _showTryAgainButton.value = true
                            _eventFlow.emit(
                                UIEvent.ShowSnackBar(
                                    it.message ?: "Failed to load exercise list. Please try again."
                                )
                            )
                        }

                        is Resource.Loading -> {
                            _isExerciseLoading.value = true
                        }

                        is Resource.Success -> {
                            originalExerciseList = it.data ?: emptyList()
                            it.data?.let { exercises ->
//                                setExerciseList(testId = testId, exercises = exercises)
                                _exercises.value = exercises.sortedBy { it.name }
                            }
                            _isExerciseLoading.value = false
                        }
                    }
                }.launchIn(this)
        }
    }

    //    private fun setExerciseList(testId: String, exercises: List<Exercise>) {
//        for (index in 0.._assessments.value.size) {
//            if (_assessments.value[index].testId == testId) {
//                _assessments.value[index].exercises = exercises
//                _exercises.value = exercises.sortedBy { it.name }
//                break
//            }
//        }
//    }
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

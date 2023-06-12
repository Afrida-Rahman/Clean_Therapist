package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.usecase.networkData.ExerciseUseCases
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera.Permission.GetCameraPermissions
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera.Permission.isCameraPermissionsGranted
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.exercise.ExerciseAnalyzer
import javax.inject.Inject

@HiltViewModel
class ExerciseScreenViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases
) : ViewModel() {

    var exerciseAnalyzer = ExerciseAnalyzer()

    @Composable
    fun CheckAndGetPermission(
        context: Context, launcher: ManagedActivityResultLauncher<Array<String>,
                Map<String, @JvmSuppressWildcards Boolean>>
    ) {
        if (!isCameraPermissionsGranted(context)) {
            GetCameraPermissions(launcher)
        }
    }

    fun bindAllCameraUseCases(context: Context, lifecycleOwner: LifecycleOwner) {
        exerciseAnalyzer.buildExerciseAnalyzer(context = context, lifecycleOwner = lifecycleOwner)
    }

    fun setExerciseConstraints(
        context: Context,
        tenant: String,
        exercise: Exercise,
        navController: NavController
    ) {
        exerciseAnalyzer.initializeExercise(context, exercise)
        fetchExerciseConstraints(tenant, context, exercise.id, navController)
    }

    private fun fetchExerciseConstraints(
        tenant: String,
        context: Context,
        exerciseId: Int,
        navController: NavController
    ) {
        viewModelScope.launch {
            exerciseUseCases.fetchExerciseConstraints(
                tenant = tenant,
                exerciseId = exerciseId
            ).onEach {
                when (it) {
                    is Resource.Error -> {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }

                    is Resource.Success -> {
                        it.data?.let { phases ->
                            exerciseAnalyzer.updateExerciseConstraints(phases)
                        }
                    }

                    else -> {}
                }
            }.launchIn(this)
        }
    }

    fun onResume(context: Context, lifecycleOwner: LifecycleOwner) {
        bindAllCameraUseCases(context, lifecycleOwner)
    }

    fun onPause() {
        exerciseAnalyzer.imageProcessor?.run { this.stop() }
    }

    fun onDestroy() {
        exerciseAnalyzer.imageProcessor?.run { this.stop() }
    }
}
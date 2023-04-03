package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.app.Application
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonViewModel

@Composable
fun ExerciseScreen(
    tenant: String,
    testId: String,
    exerciseId: Int,
    navController: NavController,
    commonViewModel: CommonViewModel,
    viewModel: ExerciseScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val lifecycleOwner = LocalLifecycleOwner.current

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            println("${it.key} -> ${it.value}")
        }
    }

    viewModel.CheckAndGetPermission(context, launcher)

    viewModel.cameraSelector =
        CameraSelector.Builder().requireLensFacing(viewModel.lensFacing).build()
    ViewModelProvider(
        navController.getViewModelStoreOwner(navGraphId = navController.graph.id),
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    )[ExerciseViewModel::class.java]
        .processCameraProvider
        .observe(
            lifecycleOwner
        ) { provider: ProcessCameraProvider? ->
            viewModel.cameraProvider = provider
            viewModel.bindAllCameraUseCases(context, lifecycleOwner)
        }


    val scaffoldState = rememberScaffoldState()
    AndroidView(factory = {
        View.inflate(it, R.layout.activity_exercise_screen, null)
    },
        modifier = Modifier.fillMaxSize(),
        update = {
            viewModel.previewView = it.findViewById(R.id.preview_view)
            viewModel.graphicOverlay = it.findViewById(R.id.graphic_overlay)
            it.findViewById<ImageButton>(R.id.camera_switch_button)
                .setOnClickListener {
                    if (viewModel.cameraProvider == null) {
                        return@setOnClickListener
                    }
                    val newLensFacing =
                        if (viewModel.lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                    val newCameraSelector =
                        CameraSelector.Builder().requireLensFacing(newLensFacing).build()
                    try {
                        if (viewModel.cameraProvider!!.hasCamera(newCameraSelector)) {
                            viewModel.lensFacing = newLensFacing
                            viewModel.cameraSelector = newCameraSelector
//                            viewModel.bindAllCameraUseCases()
                            return@setOnClickListener
                        }
                    } catch (e: CameraInfoUnavailableException) {
                        // Falls through
                    }
                    Toast.makeText(
                        context,
                        "This device does not have lens with facing: $newLensFacing",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    )
    commonViewModel.fetchExerciseConstraints(tenant, testId, exerciseId)
    LaunchedEffect(key1 = true) {
        commonViewModel.eventFlow.collect { event ->
            when (event) {
                is UIEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
                is UIEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
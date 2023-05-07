package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.app.Application
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonViewModel
import org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.component.ImageSection

@Composable
fun ExerciseScreen(
    tenant: String,
    testId: String,
    exercise: Exercise,
    navController: NavController,
    commonViewModel: CommonViewModel,
    viewModel: ExerciseScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    viewModel.setExerciseConstraints(context, tenant, exercise)

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

    val scaffoldState = rememberScaffoldState()

    if (showDialog.value) {
        Alert(name = "Name",
            showDialog = showDialog.value,
            onDismiss = {showDialog.value = false})
    }
    AndroidView(factory = {
        View.inflate(it, R.layout.activity_exercise_screen, null)
    },
        modifier = Modifier.fillMaxSize(),
        update = {
            viewModel.previewView = it.findViewById(R.id.preview_view)
            viewModel.countDisplay = it.findViewById(R.id.right_count)
            viewModel.maxHoldTimeDisplay = it.findViewById(R.id.max_hold_time_display)
            viewModel.wrongCountDisplay = it.findViewById(R.id.wrong_count)
            viewModel.graphicOverlay = it.findViewById(R.id.graphic_overlay)
            viewModel.distanceDisplay = it.findViewById(R.id.distance)
            viewModel.distanceDisplay = it.findViewById(R.id.phase_dialogue)
            viewModel.exerciseProgressBar = it.findViewById(R.id.exercise_progress)
            viewModel.exerciseProgressBar.max = viewModel.homeExercise.maxRepCount * viewModel.homeExercise.maxSetCount

            viewModel.distanceDisplay.visibility = View.GONE

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
            val exerciseNameTV:TextView = it.findViewById(R.id.exercise_name)
            exerciseNameTV.text = exercise.name
            it.findViewById<ImageButton>(R.id.camera_switch_button)
                .setOnClickListener {
                    if (viewModel.cameraProvider == null) {
                        return@setOnClickListener
                    }
                    val newLensFacing: Int =
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
                            viewModel.bindAllCameraUseCases(context, lifecycleOwner)
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
            it.findViewById<ImageButton>(R.id.btn_gif_display)
                .setOnClickListener{showDialog.value = true}
        }
    )
    DisposableEffect(lifecycleOwner){
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME){
                viewModel.onResume(context, lifecycleOwner)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                viewModel.onPause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            viewModel.onDestroy()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

    }
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

@Composable
fun Alert(name: String, showDialog: Boolean, onDismiss: () -> Unit) {
    var urls : List<String> = listOf("https://mmhva.s3.amazonaws.com/Images%2fRS%2fEvalExercise%2f78b269fe-e9d6-4d2f-a191-d064a95b6df6.png", "https://mmhva.s3.amazonaws.com/Images%2fRS%2fEvalExercise%2f0481b0cb-09a9-4430-bf02-af585e8f42e7.png", "https://mmhva.s3.amazonaws.com/EvalExerciseList%2fShoulder+abduction+in+standing-e1122b9f-93ab-4ae9-b934-dc7ee6184716.gif")
    if (showDialog) {
        AlertDialog(
            text = {
                ImageSection(urls)
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss ) {
                    Text("OK")
                }
            },
            dismissButton = {}
        )
    }
}

package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.app.Application
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
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
    exercise: Exercise,
    navController: NavController,
    commonViewModel: CommonViewModel,
    viewModel: ExerciseScreenViewModel = hiltViewModel()
) {
    val showCongrats: Boolean by viewModel.exerciseAnalyzer.showCongrats.observeAsState(initial = false)
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }

    var pauseButton: Button?
    var resumeButton: Button?
    var pauseIndicator: ImageView?

    viewModel.setExerciseConstraints(context, tenant, exercise, navController)

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
        Alert(urls = exercise.imageURLs,
            showDialog = showDialog.value,
            onDismiss = { showDialog.value = false })
    }
    AndroidView(factory = {
        View.inflate(it, R.layout.activity_exercise_screen, null)
    },
        modifier = Modifier.fillMaxSize(),
        update = {
            viewModel.exerciseAnalyzer.previewView = it.findViewById(R.id.preview_view)
            viewModel.exerciseAnalyzer.countDisplay = it.findViewById(R.id.right_count)
            viewModel.exerciseAnalyzer.maxHoldTimeDisplay =
                it.findViewById(R.id.max_hold_time_display)
            viewModel.exerciseAnalyzer.wrongCountDisplay = it.findViewById(R.id.wrong_count)
            viewModel.exerciseAnalyzer.graphicOverlay = it.findViewById(R.id.graphic_overlay)
            viewModel.exerciseAnalyzer.distanceDisplay = it.findViewById(R.id.distance)
            viewModel.exerciseAnalyzer.phaseDialogueDisplay = it.findViewById(R.id.phase_dialogue)
            viewModel.exerciseAnalyzer.exerciseProgressBar = it.findViewById(R.id.exercise_progress)
            pauseButton = it.findViewById(R.id.btn_pause)
            resumeButton = it.findViewById(R.id.btn_resume)
            pauseIndicator = it.findViewById(R.id.pause_indicator)
            viewModel.exerciseAnalyzer.exerciseProgressBar.max =
                viewModel.exerciseAnalyzer.homeExercise.maxRepCount * viewModel.exerciseAnalyzer.homeExercise.maxSetCount

            viewModel.exerciseAnalyzer.cameraSelector =
                CameraSelector.Builder().requireLensFacing(viewModel.exerciseAnalyzer.lensFacing)
                    .build()
            ViewModelProvider(
                navController.getViewModelStoreOwner(navGraphId = navController.graph.id),
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[ExerciseViewModel::class.java]
                .processCameraProvider
                .observe(
                    lifecycleOwner
                ) { provider: ProcessCameraProvider? ->
                    viewModel.exerciseAnalyzer.cameraProvider = provider
                    viewModel.bindAllCameraUseCases(context, lifecycleOwner)
                }
            val exerciseNameTV: TextView = it.findViewById(R.id.exercise_name)
            exerciseNameTV.text = exercise.name
            it.findViewById<ImageButton>(R.id.camera_switch_button)
                .setOnClickListener {
                    if (viewModel.exerciseAnalyzer.cameraProvider == null) {
                        return@setOnClickListener
                    }
                    val newLensFacing: Int =
                        if (viewModel.exerciseAnalyzer.lensFacing == CameraSelector.LENS_FACING_FRONT) {
                            CameraSelector.LENS_FACING_BACK
                        } else {
                            CameraSelector.LENS_FACING_FRONT
                        }
                    val newCameraSelector =
                        CameraSelector.Builder().requireLensFacing(newLensFacing).build()
                    try {
                        if (viewModel.exerciseAnalyzer.cameraProvider!!.hasCamera(newCameraSelector)) {
                            viewModel.exerciseAnalyzer.lensFacing = newLensFacing
                            viewModel.exerciseAnalyzer.cameraSelector = newCameraSelector
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
                .setOnClickListener { showDialog.value = true }
            pauseButton?.setOnClickListener {
                pauseButton?.visibility = View.GONE
                resumeButton?.visibility = View.VISIBLE
                pauseIndicator?.visibility = View.VISIBLE
                viewModel.exerciseAnalyzer.homeExercise.pauseExercise()
//                viewModel.onEvent(ExerciseEvent.PauseResumeExercise)
            }
            resumeButton?.setOnClickListener {
                resumeButton?.visibility = View.GONE
                pauseIndicator?.visibility = View.GONE
                pauseButton?.visibility = View.VISIBLE
                viewModel.exerciseAnalyzer.homeExercise.resumeExercise()
            }

        }
    )
    if (showCongrats) {
        ShowCongrats(onDismiss = {
            navController.popBackStack()
        })
    }
//
//    if (showResumeBtn){
//        "Resume".also { pauseButton?.text = it }
//        viewModel.homeExercise.pauseExercise()
//    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
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
fun Alert(urls: List<String>, showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            text = {
                ImageSection(urls)
            },
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            dismissButton = {}
        )
    }
}

@Composable
fun ShowCongrats(onDismiss: () -> Unit) {
    AlertDialog(
        text = {
            Text(
                text = "Congratulations! You have successfully completed the exercise. Please be prepared for the next one.",
                color = colorResource(id = R.color.black)
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Ok")
            }
        },
        dismissButton = {}
    )
}

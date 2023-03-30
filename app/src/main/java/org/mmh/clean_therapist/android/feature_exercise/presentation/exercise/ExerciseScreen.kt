package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
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
){
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            println("${it.key} -> ${it.value}")
        }
    }

    viewModel.checkAndGetPermission(context, launcher)

    val scaffoldState = rememberScaffoldState()
    AndroidView(factory = {
        View.inflate(it, R.layout.activity_exercise_screen, null)
    },
        modifier = Modifier.fillMaxSize(),
        update = {
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
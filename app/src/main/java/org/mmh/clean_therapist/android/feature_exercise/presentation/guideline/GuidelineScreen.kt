package org.mmh.clean_therapist.android.feature_exercise.presentation.guideline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.component.CustomTopAppBar
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonViewModel
import org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.component.ImageSection
import org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.component.InstructionSection
import org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.component.VideoSection

@Composable
fun GuidelineScreen(
    tenant: String,
    testId: String,
    exerciseId: Int,
    navController: NavController,
    commonViewModel: CommonViewModel
) {
    val scaffoldState = rememberScaffoldState()
    val exercise = commonViewModel.getExercise(testId = testId, exerciseId = exerciseId)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CustomTopAppBar(
                leadingIcon = R.drawable.ic_arrow_back,
                onClickLeadingIcon = { navController.popBackStack() }
            ) {
                Text(
                    text = "Exercise Guideline",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            exercise?.let { exercise ->
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.h1,
                    overflow = TextOverflow.Ellipsis
                )
                Button(onClick = {
                    navController.popBackStack()
                    navController.navigate(
                        Screen.ExerciseScreen.withArgs(
                            tenant,
                            testId,
                            exercise.name,
                            exercise.id.toString(),
                        )
                    )
                }) {
                    Text(text = "Start Workout")
                }
                InstructionSection(exercise.instruction)
                ImageSection(exercise.imageURLs)
                exercise.videoURL?.let { url ->
                    VideoSection(videoUrl = url)
                }
            }
        }
    }
}
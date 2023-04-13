package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.core.component.CustomTopAppBar
import org.mmh.clean_therapist.android.core.component.Pill
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_exercise.domain.model.toJson
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonEvent
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonViewModel
import org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.component.ExerciseCard
import org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.component.ExerciseFilter
import org.mmh.clean_therapist.android.ui.theme.Yellow

@Composable
fun ExerciseListScreen(
    tenant: String,
    testId: String,
    creationDate: String,
    navController: NavController,
    commonViewModel: CommonViewModel,
    viewModel: ExerciseListViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    var showExerciseFilter by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val localConfiguration = LocalConfiguration.current
    val itemsPerRow = when {
        localConfiguration.screenWidthDp > 840 -> {
            3
        }
        localConfiguration.screenWidthDp > 600 -> {
            2
        }
        else -> {
            1
        }
    }
    commonViewModel.loadExercises(testId = testId, tenant = tenant)

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
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

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CustomTopAppBar(
                leadingIcon = R.drawable.ic_arrow_back,
                onClickLeadingIcon = {
                    navController.popBackStack()
                },
                trailingIcon = if (showExerciseFilter) {
                    R.drawable.ic_cross
                } else {
                    R.drawable.search
                },
                onClickTrailingIcon = { showExerciseFilter = !showExerciseFilter },
                extraContent = {
                    AnimatedVisibility(visible = showExerciseFilter) {
                        ExerciseFilter {
                            commonViewModel.onEvent(
                                CommonEvent.ApplyExerciseFilter(
                                    testId = testId,
                                    exerciseName = it
                                )
                            )
                            showExerciseFilter = false
                        }
                    }
                }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Home Exercises",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        Pill(
                            text = testId,
                            textColor = Color.Black,
                            backgroundColor = Yellow
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Pill(
                            text = creationDate,
                            textColor = Color.Black,
                            backgroundColor = Yellow
                        )
                    }
                }
            }
        },
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            when {
                commonViewModel.isExerciseLoading.value -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
                commonViewModel.showTryAgain.value -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Button(onClick = {
                            commonViewModel.onEvent(
                                CommonEvent.FetchExercises(
                                    testId = testId,
                                    tenant = tenant
                                )
                            )
                        }) {
                            Text(text = "Try Again")
                        }
                    }
                }
                else -> {
                    commonViewModel.exercises.value?.let { exercises ->
                        if (exercises.isNotEmpty()) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(itemsPerRow),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                items(exercises) {
                                    ExerciseCard(
                                        imageUrls = it.imageURLs,
                                        name = it.name,
                                        repetition = it.repetition,
                                        set = it.set,
                                        onGuidelineButtonClicked = {
                                            navController.navigate(
                                                Screen.GuidelineScreen.withArgs(
                                                    tenant,
                                                    testId,
                                                    it.id.toString()
                                                )
                                            )
                                        },
                                        onStartWorkoutButtonClicked = {
                                            navController.navigate(
                                                Screen.ExerciseScreen.withArgs(
                                                    tenant,
                                                    testId,
                                                    it.toJson()
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "No exercise is assigned yet!",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
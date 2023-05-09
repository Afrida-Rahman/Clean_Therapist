package org.mmh.clean_therapist.android.feature_exercise.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import org.mmh.clean_therapist.android.core.util.EXERCISE_ROUTE
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.model.fromJson
import org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.AssessmentListScreen
import org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.AssessmentListViewModel
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.ExerciseScreen
import org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.ExerciseListScreen
import org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.ExerciseListViewModel
import org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.GuidelineScreen

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.exerciseNav(navController: NavController) {
    lateinit var commonViewModel: CommonViewModel
    lateinit var exerciseListViewModel: ExerciseListViewModel
    lateinit var assessmentListViewModel: AssessmentListViewModel

    navigation(route = EXERCISE_ROUTE, startDestination = Screen.AssessmentListScreen.route) {
        composable(route = Screen.AssessmentListScreen.route) {
            assessmentListViewModel = hiltViewModel()
            AssessmentListScreen(navController = navController, viewModel = assessmentListViewModel)
        }
        composable(
            route = Screen.ExerciseListScreen.route + "/{tenant}/{testId}/{creationDate}",
            arguments = listOf(
                navArgument(name = "tenant") {
                    type = NavType.StringType
                },
                navArgument(name = "testId") {
                    type = NavType.StringType
                },
                navArgument(name = "creationDate") {
                    type = NavType.StringType
                }
            )
        ) {
            exerciseListViewModel = hiltViewModel()
            it.arguments?.getString("tenant")?.let { tenant ->
                it.arguments?.getString("testId")?.let { testId ->
                    it.arguments?.getString("creationDate")?.let { creationDate ->
                        ExerciseListScreen(
                            tenant = tenant,
                            testId = testId,
                            creationDate = creationDate,
                            navController = navController,
                            viewModel = exerciseListViewModel
                        )
                    }
                }
            }
        }
        composable(
            route = Screen.GuidelineScreen.route + "/{tenant}/{testId}/{exercise}",
            arguments = listOf(
                navArgument(name = "tenant") {
                    type = NavType.StringType
                },
                navArgument(name = "testId") {
                    type = NavType.StringType
                },
                navArgument(name = "exercise") {
                    type = NavType.StringType
                }
            )
        ) {
            it.arguments?.getString("tenant")?.let { tenant ->
                it.arguments?.getString("testId")?.let { testId ->
                    it.arguments?.getString("exercise")?.let { exercise ->
                        GuidelineScreen(
                            tenant = tenant,
                            testId = testId,
                            exercise = exercise.replace("\$\$\$", "/")
                                .fromJson(Exercise::class.java),
                            navController = navController,
                            commonViewModel = commonViewModel
                        )
                    }
                }
            }
        }
        composable(
            route = Screen.ExerciseScreen.route + "/{tenant}/{testId}/{exercise}",
            arguments = listOf(
                navArgument(name = "tenant") {
                    type = NavType.StringType
                },
                navArgument(name = "testId") {
                    type = NavType.StringType
                },
                navArgument(name = "exercise") {
                    type = NavType.StringType
                }
            )
        ) {
            it.arguments?.getString("tenant")?.let { tenant ->
                it.arguments?.getString("testId")?.let { testId ->
                    it.arguments?.getString("exercise")?.let { exercise ->
                        ExerciseScreen(
                            tenant = tenant,
                            testId = testId,
                            exercise = exercise.replace("\$\$\$", "/")
                                .fromJson(Exercise::class.java),
                            navController = navController,
                            commonViewModel = commonViewModel
                        )
                    }
                }
            }
        }
    }
}
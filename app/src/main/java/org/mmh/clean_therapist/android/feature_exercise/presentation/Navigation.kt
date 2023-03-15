package org.mmh.clean_therapist.android.feature_exercise.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import org.mmh.clean_therapist.android.core.util.EXERCISE_ROUTE
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.AssessmentListScreen
import org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.ExerciseListScreen

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.exerciseNav(navController: NavController) {
    lateinit var commonViewModel: CommonViewModel

    navigation(route = EXERCISE_ROUTE, startDestination = Screen.AssessmentListScreen.route) {
        composable(route = Screen.AssessmentListScreen.route) {
            commonViewModel = hiltViewModel()
            AssessmentListScreen(navController = navController, viewModel = commonViewModel)
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
            it.arguments?.getString("tenant")?.let { tenant ->
                it.arguments?.getString("testId")?.let { testId ->
                    it.arguments?.getString("creationDate")?.let { creationDate ->
                        ExerciseListScreen(
                            tenant = tenant,
                            testId = testId,
                            creationDate = creationDate,
                            navController = navController,
                            commonViewModel = commonViewModel
                        )
                    }
                }
            }
        }
    }
}
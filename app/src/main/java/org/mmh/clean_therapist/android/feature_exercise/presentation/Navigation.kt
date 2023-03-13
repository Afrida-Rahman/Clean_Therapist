package org.mmh.clean_therapist.android.feature_exercise.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.composable
import org.mmh.clean_therapist.android.core.util.EXERCISE_ROUTE
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.AssessmentListScreen

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.exerciseNav(navController: NavController) {
    lateinit var commonViewModel: CommonViewModel

    navigation(route = EXERCISE_ROUTE, startDestination = Screen.AssessmentListScreen.route) {
        composable(route = Screen.AssessmentListScreen.route) {
            commonViewModel = hiltViewModel()
            AssessmentListScreen(navController = navController, viewModel = commonViewModel)
        }
    }
}
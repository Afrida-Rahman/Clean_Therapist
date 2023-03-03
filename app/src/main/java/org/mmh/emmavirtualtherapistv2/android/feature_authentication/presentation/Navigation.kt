package org.mmh.emmavirtualtherapistv2.android.feature_authentication.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.mmh.emmavirtualtherapistv2.android.core.util.AUTHENTICATION_ROUTE
import org.mmh.emmavirtualtherapistv2.android.core.util.Screen
import org.mmh.emmavirtualtherapistv2.android.feature_authentication.presentation.splash.SplashScreen

fun NavGraphBuilder.authenticationNav(navController: NavController) {
    navigation(startDestination = Screen.SplashScreen.route, route = AUTHENTICATION_ROUTE) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
    }
}
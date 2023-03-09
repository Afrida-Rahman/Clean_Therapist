package org.mmh.clean_therapist.android.feature_authentication.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import org.mmh.clean_therapist.android.core.util.AUTHENTICATION_ROUTE
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_authentication.presentation.sign_in.SignInScreen
import org.mmh.clean_therapist.android.feature_authentication.presentation.splash.SplashScreen
import org.mmh.clean_therapist.android.feature_authentication.presentation.walkthrough.WalkThroughScreen
import org.mmh.clean_therapist.android.feature_authentication.presentation.welcome.WelcomeScreen

fun NavGraphBuilder.authenticationNav(navController: NavController) {
    navigation(startDestination = Screen.SplashScreen.route, route = AUTHENTICATION_ROUTE) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.WalkThroughScreen.route) {
            WalkThroughScreen(navController = navController)
        }
        composable(route = Screen.WelcomeScreen.route) {
            WelcomeScreen(navController = navController)
        }
        composable(route = Screen.SignInScreen.route) {
            SignInScreen(navController = navController)
        }
    }
}
package org.mmh.clean_therapist.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import org.mmh.clean_therapist.android.core.util.AUTHENTICATION_ROUTE
import org.mmh.clean_therapist.android.core.util.ROOT_ROUTE
import org.mmh.clean_therapist.android.feature_authentication.presentation.authenticationNav
import org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseNav
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EmmaVirtualTherapistTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = AUTHENTICATION_ROUTE,
                    route = ROOT_ROUTE
                ) {
                    authenticationNav(navController = navController)
                    exerciseNav(navController = navController)
                }
            }
        }
    }
}
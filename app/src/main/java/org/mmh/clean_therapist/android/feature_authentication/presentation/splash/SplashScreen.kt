package org.mmh.clean_therapist.android.feature_authentication.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.mmh.clean_therapist.android.core.util.Screen
import kotlinx.coroutines.delay
import org.mmh.clean_therapist.R

@Composable
fun SplashScreen(navController: NavController, viewModel: SplashScreenViewModel = hiltViewModel()) {
    val duration = 2000
    var startAnimation by remember { mutableStateOf(false) }
    val animation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = duration)
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay((duration - 100).toLong())
        navController.popBackStack()
        if (!viewModel.isWalkThroughShown.value) {
            navController.navigate(Screen.WalkThroughScreen.route)
        } else if (viewModel.isAlreadyLoggedIn.value) {
            navController.navigate(Screen.DashboardScreen.route)
        } else {
            navController.navigate(Screen.WelcomeScreen.route)
        }
    }
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = "MyMedicalHUB",
            alpha = animation.value,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillWidth
        )
        Image(
            painter = painterResource(id = R.drawable.logo_white),
            contentDescription = "MyMedicalHUB",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.7f)
        )
        Box(
            modifier = Modifier
                .padding(8.dp)
                .width(150.dp)
                .height(8.dp)
                .background(
                    color = MaterialTheme.colors.onPrimary,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}
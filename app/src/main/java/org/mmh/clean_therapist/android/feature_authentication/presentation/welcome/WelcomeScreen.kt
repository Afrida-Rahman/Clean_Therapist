package org.mmh.clean_therapist.android.feature_authentication.presentation.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_authentication.presentation.welcome.components.ActionCard
import org.mmh.clean_therapist.android.ui.theme.Blue900
import org.mmh.clean_therapist.android.ui.theme.Green
import org.mmh.clean_therapist.R

@Composable
fun WelcomeScreen(
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(44.dp)
    ) {

        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(id = R.drawable.mmh),
            contentDescription = "MyMedicalHUB",
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Our health technology solution enables physicians to identify patient risk for injuring any of the ove 350 joints in the human body, avoid pain, minimize healthcare costs and improve wellness.",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        ActionCard(
            icon = R.drawable.user_tick,
            text = "Returning Patient",
            backgroundColor = Blue900
        ) {
            navController.navigate(Screen.SignInScreen.route)
        }
        Spacer(modifier = Modifier.height(24.dp))
        ActionCard(
            icon = R.drawable.user,
            text = "New Patient",
            backgroundColor = Green
        ) { navController.navigate(Screen.SignUpScreen.route) }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Text(text = "Don't have account?")
            Text(
                text = "Sign Up",
                color = Color(0xFF1176B4),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        navController.navigate(Screen.SignUpScreen.route)
                    }
            )
        }
    }
}
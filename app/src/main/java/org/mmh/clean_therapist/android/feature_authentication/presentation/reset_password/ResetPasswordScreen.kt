package org.mmh.clean_therapist.android.feature_authentication.presentation.reset_password

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.component.CustomTopAppBar
import org.mmh.clean_therapist.android.core.component.OutlineInputTextField
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun ResetPasswordScreen(navController: NavController) {
    val scaffoldState = rememberScaffoldState()
    val field = remember {
        mutableStateOf("")
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            CustomTopAppBar(
                leadingIcon = R.drawable.ic_arrow_back,
                onClickLeadingIcon = { navController.popBackStack() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mmh),
                    contentDescription = "MyMedicalHub",
                    modifier = Modifier
                        .height(50.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.h2,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter your email and we’ll send an instructions\n" +
                            "to reset your password",
                    style = MaterialTheme.typography.body2,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(20.dp))
                OutlineInputTextField(
                    field = field,
                    placeholder = "Enter your email",
                    label = "Email Address",
                    leadingIcon = R.drawable.email_gray
                )
                Spacer(modifier = Modifier.height(80.dp))
                Button(
                    onClick = {
                        navController.popBackStack()
                        navController.navigate(Screen.RecoveryPasswordScreen.route)
                    },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primaryVariant,
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .width(150.dp)
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colors.onBackground,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordScreenPreview() {
    EmmaVirtualTherapistTheme {
        ResetPasswordScreen(rememberNavController())
    }
}
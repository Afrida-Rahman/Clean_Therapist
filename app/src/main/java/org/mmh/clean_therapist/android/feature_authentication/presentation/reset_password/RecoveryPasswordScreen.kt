package org.mmh.clean_therapist.android.feature_authentication.presentation.reset_password

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun RecoveryPasswordScreen(navController: NavController) {
    val scaffoldState = rememberScaffoldState()
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
            Spacer(modifier = Modifier.height(76.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.recovery_password),
                    contentDescription = "MyMedicalHUB",
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Check your email",
                    style = MaterialTheme.typography.h2,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "We’ve sent a password recover\n" +
                            "instructions to your email",
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(80.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primaryVariant,
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        text = "Okay!",
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
fun RecoveryPasswordScreenPreview() {
    EmmaVirtualTherapistTheme {
        RecoveryPasswordScreen(rememberNavController())
    }
}
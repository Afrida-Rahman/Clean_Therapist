package org.mmh.clean_therapist.android.feature_authentication.presentation.sign_up

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun SignUpScreen(navController: NavController) {
    val scaffoldState = rememberScaffoldState()
    val isButtonEnable = false
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
                    .weight(1f)
            ) {
                OutlineInputTextField(
                    field = field,
                    placeholder = "Enter your first name",
                    label = "First Name",
                    leadingIcon = R.drawable.user_gray
                )
                OutlineInputTextField(
                    field = field,
                    placeholder = "Enter your Last name",
                    label = "Last Name",
                    leadingIcon = R.drawable.user_gray
                )
                OutlineInputTextField(
                    field = field,
                    placeholder = "Enter your email address",
                    label = "Email Address",
                    leadingIcon = R.drawable.email_gray
                )
                OutlineInputTextField(
                    field = field,
                    placeholder = "Enter password",
                    label = "Password",
                    leadingIcon = R.drawable.lock_gray,
                    trailingIcon = R.drawable.eye_open_gray
                )
                OutlineInputTextField(
                    field = field,
                    placeholder = "Confirm password",
                    label = "Confirm Password",
                    leadingIcon = R.drawable.lock_gray,
                    trailingIcon = R.drawable.eye_close_gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = false,
                        onCheckedChange = {},
                    )
                    Row {
                        Text(
                            text = "I Agree with ",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Terms ",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primaryVariant
                        )
                        Text(
                            text = "and ",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.body2,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.primaryVariant
                        )
                    }
                }
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primaryVariant,
                        disabledBackgroundColor = Color(0xFFEFF6FD)
                    ),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold,
                        color = if (isButtonEnable) MaterialTheme.colors.onPrimary else MaterialTheme.colors.primaryVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.body1,
                    color = Color.LightGray
                )
                Text(
                    text = "Sign In",
                    color = MaterialTheme.colors.primaryVariant,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .clickable {
                            navController.navigate(Screen.SignInScreen.route)
                        }
                )
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
fun SignUpScreenPreview() {
    EmmaVirtualTherapistTheme {
        SignUpScreen(rememberNavController())
    }
}
package org.mmh.clean_therapist.android.feature_authentication.presentation.sign_in

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.core.component.CustomTopAppBar
import org.mmh.clean_therapist.android.core.component.OutlineInputTextField
import org.mmh.clean_therapist.android.core.util.Screen

@Composable
fun SignInScreen(
    navController: NavController,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val keyboardService = LocalTextInputService.current
    val tenants = listOf("emma", "npc")
    var selectedTenant by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(Size.Zero) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UIEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message)
                }
                is UIEvent.ShowToastMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
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
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {

                Text(text = "Select Your Practice")
                OutlinedTextField(
                    value = selectedTenant.uppercase(),
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { position ->
                            size = position.size.toSize()
                        },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { expanded = !expanded }
                        )
                    },
                    placeholder = { Text("Select Your Practice") },
                    shape = RoundedCornerShape(16.dp),
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(with(LocalDensity.current) { size.width.toDp() })
                ) {
                    tenants.forEach {
                        DropdownMenuItem(onClick = {
                            selectedTenant = it
                            expanded = false
                            viewModel.onEvent(SignInEvent.EnteredTenant(it))
                        }) {
                            Text(text = it.uppercase())
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Email Address")
                OutlineInputTextField(
                    field = viewModel.email,
                    leadingIcon = R.drawable.email_gray,
                    onValueChange = {
                        viewModel.onEvent(SignInEvent.EnteredEmail(it))
                    },
                    onIconPressed = {},
                    placeholder = "Email",
                    keyboardType = KeyboardType.Text
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Password")
                OutlineInputTextField(
                    field = viewModel.password,
                    onValueChange = {
                        viewModel.onEvent(SignInEvent.EnteredPassword(it))
                    },
                    leadingIcon = R.drawable.lock_gray,
                    trailingIcon = if (viewModel.showPassword.value) {
                        R.drawable.eye_open_gray
                    } else {
                        R.drawable.eye_close_gray
                    },
                    onIconPressed = {
                        viewModel.onEvent(SignInEvent.ShowPassword)
                    },
                    placeholder = "Password",
                    keyboardType = KeyboardType.Password,
                    visualTransformation = if (viewModel.showPassword.value) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardService?.hideSoftwareKeyboard()
                        viewModel.onEvent(SignInEvent.SignInButtonClick {
                            navController.popBackStack()
                            navController.navigate(Screen.DashboardScreen.route)
                        })
                    })
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Forget Password?",
                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(Screen.ResetPasswordScreen.route)
                        }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.onEvent(SignInEvent.SignInButtonClick {
                            navController.popBackStack()
                            navController.navigate(Screen.DashboardScreen.route)
                        })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                ) {
                    if (viewModel.showCircularProgressIndicator.value) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Text(
                            text = "Sign In",
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text(text = "Don't have account? ")
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
    }
}


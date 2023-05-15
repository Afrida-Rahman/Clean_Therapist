package org.mmh.clean_therapist.android.feature_authentication.presentation.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.component.CustomTopAppBar
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.feature_authentication.presentation.settings.component.AccountSettingSection
import org.mmh.clean_therapist.android.feature_authentication.presentation.settings.component.GeneralSettingSection
import org.mmh.clean_therapist.android.feature_authentication.presentation.settings.component.ProfileHeader
import org.mmh.clean_therapist.android.feature_authentication.presentation.settings.component.SettingsItem
import org.mmh.clean_therapist.android.ui.theme.Red
import org.mmh.clean_therapist.android.ui.theme.Red200

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val patient = viewModel.patient
    val fullName = "${patient.value?.firstName} ${patient.value?.lastName}"
    Scaffold(
        topBar = {
            CustomTopAppBar(
                leadingIcon = R.drawable.ic_arrow_back,
                onClickLeadingIcon = { navController.popBackStack() }
            ) {
                Text(text = "Settings", fontWeight = FontWeight.Bold)
            }
        }
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                fullName = fullName,
                email = patient.value?.email ?: "",
                tenant = patient.value?.tenant ?: ""
            )
            AccountSettingSection(navController = navController)
            GeneralSettingSection(navController = navController)
            Spacer(modifier = Modifier.height(46.dp))
            SettingsItem(
                text = "Sign Out",
                textColor = Red,
                leadingIcon = R.drawable.log_out,
                leadingIconColor = Red,
                trailingIcon = R.drawable.arrow_forward,
                iconBackgroundColor = Red200,
                onClick = {
                    navController.popBackStack()
                    navController.popBackStack()
                    navController.navigate(Screen.SignInScreen.route)
                }
            )
        }
    }
}
package org.mmh.clean_therapist.android.feature_authentication.presentation.settings.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.util.Screen
import org.mmh.clean_therapist.android.ui.theme.DarkCharcoal
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun GeneralSettingSection(navController: NavController) {
    Column {
        Text(
            text = "GENERAL",
            style = MaterialTheme.typography.caption,
            color = DarkCharcoal,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp)
        )
        SettingsItem(
            text = "My Assessment",
            leadingIcon = R.drawable.heart,
            onClick = {
                navController.popBackStack()
                navController.popBackStack()
                navController.navigate(Screen.AssessmentListScreen.route)
            }
        )
        SettingsItem(
            text = "My Exercises",
            leadingIcon = R.drawable.dashboard
        )
        SettingsItem(
            text = "My Report",
            leadingIcon = R.drawable.report_2
        )
        SettingsItem(
            text = "Audio",
            leadingIcon = R.drawable.moon,
            trailingIcon = R.drawable.toggle
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GeneralSettingSectionPreview() {
    EmmaVirtualTherapistTheme {
        GeneralSettingSection(rememberNavController())
    }
}
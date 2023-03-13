package org.mmh.clean_therapist.android.feature_authentication.presentation.settings.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.ui.theme.DarkCharcoal
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme
import org.mmh.clean_therapist.android.ui.theme.Gray200
import org.mmh.clean_therapist.android.ui.theme.Gray600

@Composable
fun SettingsItem(
    text: String,
    textColor: Color = Color.Black,
    @DrawableRes leadingIcon: Int,
    leadingIconColor: Color = DarkCharcoal,
    @DrawableRes trailingIcon: Int? = null,
    trailingIconColor: Color = Gray600,
    iconBackgroundColor: Color = Gray200,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = leadingIcon),
                contentDescription = "Leading Icon",
                tint = leadingIconColor,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color = iconBackgroundColor, shape = CircleShape)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
        if (trailingIcon == null) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_forward),
                contentDescription = "Trailing Icon",
                tint = trailingIconColor
            )
        } else {
            Image(
                painter = painterResource(id = trailingIcon),
                contentDescription = "Trailing Icon"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsItemPreview() {
    EmmaVirtualTherapistTheme {
        SettingsItem(
            text = "Edit Profile",
            leadingIcon = R.drawable.user_gray
        )
    }
}
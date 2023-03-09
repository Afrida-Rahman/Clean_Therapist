package org.mmh.clean_therapist.android.core.component

import androidx.annotation.DrawableRes
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import org.mmh.clean_therapist.android.ui.theme.Blue500
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun PrimaryLargeButton(
    text: String,
    @DrawableRes icon: Int? = null,
    isEnable: Boolean = true,
    onClick: () -> Unit = {}
) {
    LargeButton(
        text = text,
        textColor = Color.White,
        icon = icon,
        iconColor = MaterialTheme.colors.onPrimary,
        backgroundColor = MaterialTheme.colors.primary,
        isEnable = isEnable,
        disabledBackgroundColor = Blue500,
        onClick = onClick
    )
}

@Preview(showBackground = true)
@Composable
fun PrimaryLargeButtonPreview() {
    EmmaVirtualTherapistTheme {
        PrimaryLargeButton(text = "Apply")
    }
}
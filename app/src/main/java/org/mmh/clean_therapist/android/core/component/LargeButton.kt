package org.mmh.clean_therapist.android.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mmh.clean_therapist.android.ui.theme.Blue500
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun LargeButton(
    text: String,
    textColor: Color = Color.White,
    @DrawableRes icon: Int? = null,
    iconColor: Color = MaterialTheme.colors.onPrimary,
    backgroundColor: Color = MaterialTheme.colors.primary,
    isEnable: Boolean = false,
    disabledBackgroundColor: Color = Blue500,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            disabledBackgroundColor = disabledBackgroundColor
        ),
        shape = CircleShape,
        enabled = isEnable,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        icon?.let {
            Icon(painter = painterResource(id = icon), contentDescription = text, tint = iconColor)
        }
        Text(
            text = text,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LargeButtonPreview() {
    EmmaVirtualTherapistTheme {
        LargeButton(text = "Apply")
    }
}
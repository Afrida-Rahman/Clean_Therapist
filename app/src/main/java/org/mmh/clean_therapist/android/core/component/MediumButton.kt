package org.mmh.clean_therapist.android.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun MediumButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    @DrawableRes icon: Int,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(height = 40.dp, width = 166.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        )
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = text,
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = textColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MediumButtonPreview() {
    EmmaVirtualTherapistTheme {
        MediumButton(
            text = "Home Exercise",
            textColor = Color.White,
            backgroundColor = Color.Red,
            icon = R.drawable.run,
            iconColor = Color.White
        )
    }
}
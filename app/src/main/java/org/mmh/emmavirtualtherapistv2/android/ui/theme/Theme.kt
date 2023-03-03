package org.mmh.emmavirtualtherapistv2.android.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Blue900,
    primaryVariant = DarkBlue,
    onPrimary = Color.White,
    secondary = Green,
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = Gray900,
    surface = Blue100,
    onSurface = Gray900,
    error = Red,
    onError = Color.White,
)

@Composable
fun EmmaVirtualTherapistTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
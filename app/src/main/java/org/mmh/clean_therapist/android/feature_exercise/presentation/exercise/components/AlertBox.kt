package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.components

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.component.ImageSection

@Composable
fun AlertBox(
    showGuideline: Boolean = false,
    onDismiss: () -> Unit,
    message: String = "congrats",
    imageUrls: List<String> = listOf()
) {
    AlertDialog(
        text = {
            if (showGuideline) {
                ImageSection(imageUrls)
            }
            if (message == "congrats") {
                Text(
                    text = "Congratulations! You have successfully completed the exercise. Please be prepared for the next one.",
                    color = colorResource(id = R.color.black)
                )
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Ok")
            }
        },
        dismissButton = {}
    )

}
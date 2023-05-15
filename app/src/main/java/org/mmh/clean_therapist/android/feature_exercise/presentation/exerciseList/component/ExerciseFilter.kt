package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.component.OutlineInputTextField
import org.mmh.clean_therapist.android.core.component.PrimaryLargeButton
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun ExerciseFilter(
    onClickApply: (exerciseName: String) -> Unit = {}
) {
    val field = remember {
        mutableStateOf("")
    }
    val trailingIcon = if (field.value.isNotEmpty()) {
        R.drawable.ic_cross
    } else {
        null
    }
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Exercise Name",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
        OutlineInputTextField(
            field = field,
            placeholder = "Search here",
            onValueChange = { field.value = it },
            leadingIcon = R.drawable.search,
            trailingIcon = trailingIcon,
            onIconPressed = {
                field.value = ""
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryLargeButton(text = "Apply", onClick = { onClickApply(field.value) })
    }
}

@Preview
@Composable
fun ExerciseFilterPreview() {
    EmmaVirtualTherapistTheme {
        ExerciseFilter()
    }
}
package org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.component

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
import org.mmh.clean_therapist.android.R
import org.mmh.clean_therapist.android.core.component.OutlineInputTextField
import org.mmh.clean_therapist.android.core.component.PrimaryLargeButton
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun AssessmentFilter(
    onClickApply: (testId: String?, bodyRegion: String?) -> Unit = { _, _ -> }
) {
    val testIdField = remember {
        mutableStateOf("")
    }
    val bodyRegionField = remember {
        mutableStateOf("")
    }
    val showTestSearchTrailingIcon = testIdField.value.isNotEmpty()
    val showBodyRegionSearchTrailingIcon = bodyRegionField.value.isNotEmpty()
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Test ID",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
        OutlineInputTextField(
            field = testIdField,
            placeholder = "Search here",
            onValueChange = { testIdField.value = it },
            leadingIcon = R.drawable.search,
            trailingIcon = if (showTestSearchTrailingIcon) {
                R.drawable.ic_cross
            } else {
                null
            },
            onIconPressed = {
                testIdField.value = ""
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Body Region",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
        OutlineInputTextField(
            field = bodyRegionField,
            placeholder = "Search here",
            onValueChange = { bodyRegionField.value = it },
            leadingIcon = R.drawable.search,
            trailingIcon = if (showBodyRegionSearchTrailingIcon) {
                R.drawable.ic_cross
            } else {
                null
            },
            onIconPressed = {
                bodyRegionField.value = ""
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryLargeButton(
            text = "Apply",
            onClick = { onClickApply(testIdField.value, bodyRegionField.value) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AssessmentFilterPreview() {
    EmmaVirtualTherapistTheme {
        AssessmentFilter()
    }
}
package org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.component

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mmh.clean_therapist.android.R
import org.mmh.clean_therapist.android.core.component.MediumButton
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Assessment
import org.mmh.clean_therapist.android.ui.theme.*

@Composable
fun AssessmentCard(assessment: Assessment, onViewExerciseButtonClicked: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AssessmentCardBody(
                testId = assessment.testId,
                bodyRegion = assessment.bodyRegionName,
                Date = assessment.creationDate,
                exerciseCount = assessment.totalExercise,
                isReportReady = assessment.isReportReady,
            )

            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                MediumButton(
                    text = "Edit",
                    textColor = Color.White,
                    backgroundColor = Green,
                    icon = R.drawable.edit,
                    iconColor = Color.White,
                    onClick = {}
                )
                MediumButton(
                    text = "Track",
                    textColor = Color.White,
                    backgroundColor = Red,
                    icon = R.drawable.run,
                    iconColor = Color.White,
                    onClick = {
                        if (assessment.totalExercise > 0) {
                            onViewExerciseButtonClicked()
                        } else {
                            Toast.makeText(
                                context,
                                "No exercise is assigned yet!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                MediumButton(
                    text = "Followup",
                    textColor = Color.White,
                    backgroundColor = Blue900,
                    icon = R.drawable.calendar_outlined,
                    iconColor = Color.White,
                    onClick = {}
                )
                MediumButton(
                    text = "Report",
                    textColor = Color.White,
                    backgroundColor = Yellow,
                    icon = R.drawable.report_outlined,
                    iconColor = Color.White,
                    onClick = {}
                )
            }
            Spacer(Modifier.height(12.dp))
            MediumButton(
                text = "Movements",
                textColor = Color.White,
                backgroundColor = Color.Black,
                icon = R.drawable.movement_2,
                iconColor = Color.White,
                onClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssessmentCardPreview() {
    EmmaVirtualTherapistTheme {
        AssessmentCard(
            Assessment(
                testId = "TEST-16643",
                creationDate = "2021-05-25",
                isReportReady = false,
                providerId = "123123",
                providerName = "Rashed Monin",
                bodyRegionId = 0,
                bodyRegionName = "Full Body",
                registrationType = "In Clinic",
                totalExercise = 478,
                exercises = emptyList(),
            )
        ) {}
    }
}
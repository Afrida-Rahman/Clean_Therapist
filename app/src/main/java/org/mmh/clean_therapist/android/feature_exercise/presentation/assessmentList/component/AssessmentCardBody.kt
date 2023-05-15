package org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun AssessmentCardBody(
    testId: String,
    bodyRegion: String,
    Date: String,
    exerciseCount: Int,
    isReportReady: Boolean,
) {
    Column {
        AssessmentCardBodyItem(
            icon = painterResource(R.drawable.assessments_outlined),
            title = "Test ID",
            value = testId
        )
        AssessmentCardBodyItem(
            icon = painterResource(R.drawable.body_region),
            title = "Body Region",
            value = bodyRegion
        )
        AssessmentCardBodyItem(
            icon = painterResource(R.drawable.calendar_outlined),
            title = "Date",
            value = Date
        )

        AssessmentCardBodyItem(
            icon = painterResource(R.drawable.report_outlined),
            title = "Report",
            value = isReportReady.toString()
        )
        AssessmentCardBodyItem(
            icon = painterResource(R.drawable.ic_exercise),
            title = "Total Assigned Home Exercise",
            value = exerciseCount.toString()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AssessmentCardBodyPreview() {
    EmmaVirtualTherapistTheme {
        AssessmentCardBody(
            testId = "467",
            bodyRegion = "Full Body",
            Date = "09 Feb, 2022",
            exerciseCount = 477,
            isReportReady = false,
        )
    }
}
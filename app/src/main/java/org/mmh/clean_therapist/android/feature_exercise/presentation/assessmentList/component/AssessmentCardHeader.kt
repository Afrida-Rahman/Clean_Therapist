package org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mmh.clean_therapist.android.R
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun AssessmentCardHeader(testId: String, creationDate: String, isReportReady: Boolean) {
    val reportReadyIcon = if (isReportReady) {
        painterResource(id = R.drawable.ic_check)
    } else {
        painterResource(id = R.drawable.ic_cross)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.ic_report),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 6.dp, start = 10.dp)
                    .size(40.dp)
            )
            Column(
                modifier = Modifier.padding(start = 15.dp)
            ) {
                Text(
                    text = testId,
                    fontSize = 18.sp
                )
                Text(text = creationDate, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Image(
            painter = reportReadyIcon,
            contentDescription = "Report Ready Icon",
            modifier = Modifier
                .padding(top = 8.dp)
                .size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AssessmentCardHeaderPreview() {
    EmmaVirtualTherapistTheme {
        AssessmentCardHeader("TEST-16643", "2021-05-25", false)
    }
}
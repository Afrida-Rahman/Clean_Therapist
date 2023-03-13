package org.mmh.clean_therapist.android.feature_exercise.presentation.assessmentList.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mmh.clean_therapist.android.R
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun AssessmentCardBodyItem(icon: Painter, title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 4.dp, top = 4.dp)
        ) {
            Image(
                painter = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color = Color.White, shape = CircleShape)
                    .border(
                        BorderStroke(color = Color(117, 138, 223), width = 1.dp),
                        shape = CircleShape
                    )
                    .padding(10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween

            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = Color(0xFF000000),
                    style = MaterialTheme.typography.h2,
                )
                Text(
                    text = value,
                    textAlign = TextAlign.End,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AssessmentCardBodyItemPreview() {
    EmmaVirtualTherapistTheme {
        AssessmentCardBodyItem(
            icon = painterResource(R.drawable.ic_exercise),
            title = "Total Assigned Home Exercise",
            value = "477"
        )
    }
}
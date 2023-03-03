package org.mmh.clean_therapist.android.feature_authentication.presentation.walkthrough.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.mmh.clean_therapist.android.feature_authentication.domain.model.WalkThroughPage
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme
import org.mmh.clean_therapist.R

@Composable
fun Page(page: WalkThroughPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = page.image),
            contentDescription = "Page Image",
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(0.7f)
        )
        Text(
            text = page.title,
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
            color = Color.Gray,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PagePreview() {
    EmmaVirtualTherapistTheme {
        Page(
            page = WalkThroughPage(
                image = R.drawable.walkthrough,
                title = "Providing a revolutionary approach to musculoskeletal",
                description = "Our health technology solution enables physicians to identify patient risk for injuring any of the ove 350 joints in the human body"
            )
        )
    }
}
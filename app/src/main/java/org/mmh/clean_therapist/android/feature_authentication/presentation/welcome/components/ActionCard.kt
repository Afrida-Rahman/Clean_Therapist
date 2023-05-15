package org.mmh.clean_therapist.android.feature_authentication.presentation.welcome.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ActionCard(
    @DrawableRes icon: Int,
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .background(backgroundColor)
                .padding(vertical = 30.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(16.dp),
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = text, fontSize = 20.sp, color = Color.White)
        }
    }
}
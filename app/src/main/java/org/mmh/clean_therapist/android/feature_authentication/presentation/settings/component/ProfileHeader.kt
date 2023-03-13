package org.mmh.clean_therapist.android.feature_authentication.presentation.settings.component

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.ui.theme.DarkCharcoal
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun ProfileHeader(fullName: String, email: String, tenant: String, profileImage: String? = null) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .componentRegistry {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder(context))
            } else {
                add(GifDecoder())
            }
        }
        .build()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 32.dp)
    ) {
        Box {
            if (profileImage == null) {
                Image(
                    painter = painterResource(id = R.drawable.user_gray),
                    contentDescription = "Default User Image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colors.primaryVariant,
                            shape = CircleShape
                        )
                        .padding(12.dp),
                    contentScale = ContentScale.Fit,
                )
            } else {
                Image(
                    painter = rememberImagePainter(
                        data = profileImage,
                        builder = {
                            crossfade(true)
                            placeholder(R.drawable.ic_loading)
                        },
                        imageLoader = imageLoader
                    ),
                    contentDescription = fullName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.check),
                contentDescription = "Verified",
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colors.primaryVariant, shape = CircleShape)
                    .align(Alignment.BottomEnd)
                    .padding(4.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(text = fullName, style = MaterialTheme.typography.h3)
            Text(text = email, color = DarkCharcoal)
            Text(
                text = "Your Practice: ${tenant.uppercase()}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.primaryVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileHeaderPreview() {
    EmmaVirtualTherapistTheme {
        ProfileHeader(
            fullName = "Minerva Janero",
            email = "janero@yopmail.com",
            tenant = "emma"
        )
    }
}
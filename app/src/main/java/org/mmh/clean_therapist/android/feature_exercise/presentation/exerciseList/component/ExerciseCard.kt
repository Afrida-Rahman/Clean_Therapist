package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.component

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.core.component.SmallButton
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun ExerciseCard(
    imageUrls: List<String>,
    name: String,
    repetition: Int,
    set: Int,
    onGuidelineButtonClicked: () -> Unit,
    onStartWorkoutButtonClicked: () -> Unit = {}
) {
    val context = LocalContext.current
    val imageUrl = if (imageUrls.isNotEmpty()) {
        imageUrls.find { it.endsWith(".gif") } ?: imageUrls[0]
    } else {
        null
    }
    val imageLoader = ImageLoader.Builder(context)
        .componentRegistry {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder(context))
            } else {
                add(GifDecoder())
            }
        }
        .build()
    Card(
        elevation = 8.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .padding(4.dp),
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    Box {
                        if (imageUrl == null) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_human),
                                contentDescription = "Default Exercise Image",
                                modifier = Modifier.size(150.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Image(
                                painter = rememberImagePainter(
                                    data = imageUrl,
                                    builder = {
                                        crossfade(true)
                                        placeholder(R.drawable.ic_loading)
                                    },
                                    imageLoader = imageLoader
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(150.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Assigned Repetition: $repetition per set",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Assigned Set: $set",
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                SmallButton(text = "See Demo", onClick = onGuidelineButtonClicked)
                SmallButton(text = "Start Workout", onClick = onStartWorkoutButtonClicked)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseCardPreview() {
    EmmaVirtualTherapistTheme {
        ExerciseCard(
            imageUrls = emptyList(),
            name = "Body Weight Squat",
            repetition = 5,
            set = 3,
            onGuidelineButtonClicked = {}
        )
    }
}
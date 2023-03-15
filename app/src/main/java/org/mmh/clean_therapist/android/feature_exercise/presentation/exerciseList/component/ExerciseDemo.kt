package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.component

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExerciseDemo(
    phases: List<Phase>,
    onStartButtonClicked: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val sortedPhases = phases.sortedBy { it.id }
    val context = LocalContext.current
    val imageLoader: ImageLoader = ImageLoader.Builder(context)
        .componentRegistry {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder(context))
            } else {
                add(GifDecoder())
            }
        }.build()
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    count = sortedPhases.size,
                    modifier = Modifier
                        .height(450.dp)
                        .padding(12.dp)
                ) { index ->
                    val phase = sortedPhases[index]
                    phase.instruction?.let {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (phase.imageUrl.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                ) {
                                    Image(
                                        painter = rememberImagePainter(
                                            data = phase.imageUrl,
                                            builder = {
                                                crossfade(true)
                                                placeholder(R.drawable.ic_loading)
                                            },
                                            imageLoader = imageLoader
                                        ),
                                        contentDescription = "Phase Image ${phase.id}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .align(Alignment.Center),
                                        contentScale = ContentScale.Fit
                                    )
                                    Text(
                                        text = "${phase.id}",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                            Text(
                                text = it,
                                modifier = Modifier
                                    .height(50.dp)
                            )
                        }
                    }
                }
                Button(onClick = { onStartButtonClicked() }) {
                    Text(text = "Let's Start")
                }
            }
        }
    }
}
package org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.component

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSection(imageURLs: List<String>) {
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
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text = "Exercise Image", style = MaterialTheme.typography.h2)
        if (imageURLs.isNotEmpty()) {
            HorizontalPager(count = imageURLs.size) { index ->
                val imageUrl = imageURLs[index]
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
                        .size(250.dp)
                        .padding(horizontal = 4.dp)
                )
            }
        } else {
            Text(text = "Opps! Could not find any image for this exercise.")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageSectionPreview1() {
    EmmaVirtualTherapistTheme {
        ImageSection(listOf())
    }
}

@Preview(showBackground = true)
@Composable
fun ImageSectionPreview2() {
    EmmaVirtualTherapistTheme {
        ImageSection(listOf("ads"))
    }
}
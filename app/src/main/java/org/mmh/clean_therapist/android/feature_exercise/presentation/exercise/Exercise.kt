package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun ComposableExerciseScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color.Black)
    ) {
        var textSize by remember { mutableStateOf(35.sp) }
        Text(
            text = "AAROM SHOULDER INTERNAL ROTATION WITH DOWEL IN STANDING",
            color = colorResource(R.color.secondary_color),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(color = colorResource(R.color.blue))
                .padding(4.dp)
                .wrapContentHeight(align = Alignment.CenterVertically),
            fontSize = textSize,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            onTextLayout = { textLayoutResult ->
                val maxCurrentLineIndex: Int = textLayoutResult.lineCount - 1

                if (textLayoutResult.isLineEllipsized(maxCurrentLineIndex)) {
                    textSize = textSize.times(0.9f)
                }
            },
        )

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
//            val context = LocalContext.current
//            PreviewView(context).apply {
//                setBackgroundColor(Color.Black.hashCode())
//                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
//                scaleType = PreviewView.ScaleType.FILL_START
//                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
//                ContextCompat.getMainExecutor(context)
//            }
            val (
                display_holder, count_display,
                wrong_display, camera_switch_display,
                hold_time_display, distance_display,
                exercise_progress, time_count_display,
                phase_dialogue, btn_gif_display,
                pause_indicator,
                btn_done, btn_container
            ) = createRefs()
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(color = colorResource(R.color.black))
                    .padding(4.dp)
                    .constrainAs(display_holder) {
                        top.linkTo(parent.top)
                    }
            ) {
                Column(modifier = Modifier
                    .padding(4.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .width(110.dp)
                    .constrainAs(count_display) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(wrong_display.start)
                    }) {
                    Text(
                        text = "%d/%d",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color(0xFF3F5BC6),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "rep/set",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color(0xFF3F5BC6),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Column(modifier = Modifier
                    .padding(4.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .width(70.dp)
                    .constrainAs(wrong_display) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(count_display.end)
                        end.linkTo(wrong_display.start)
                    }) {
                    Text(
                        text = "%d",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(id = R.color.red),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "wrong",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(id = R.color.red),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Column(modifier = Modifier
                    .padding(4.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .constrainAs(camera_switch_display) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(wrong_display.end)
                        end.linkTo(hold_time_display.start)
                    }) {
                    Image(
                        painter = painterResource(R.drawable.ic_flip_camera),
                        contentDescription = "Camera Switching Button"
                    )
                }
                Column(modifier = Modifier
                    .padding(4.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .width(70.dp)
                    .constrainAs(hold_time_display) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(camera_switch_display.end)
                        end.linkTo(distance_display.start)
                    }) {
                    Text(
                        text = "%d",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(id = R.color.green),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "second",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(id = R.color.green),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Column(modifier = Modifier
                    .padding(4.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .width(70.dp)
                    .constrainAs(distance_display) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(hold_time_display.end)
                        end.linkTo(parent.end)
                    }) {
                    Text(
                        text = "%.1f",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(id = R.color.teal_200),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "feet",
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = colorResource(id = R.color.teal_200),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            LinearProgressIndicator(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .padding(start = 8.dp, end = 8.dp)
                    .constrainAs(exercise_progress) {
                        top.linkTo(display_holder.bottom)
                    },
                color = colorResource(id = R.color.nion_green),
                progress = .7f
            )
            Text(
                text = "%d",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .constrainAs(time_count_display) {
                        bottom.linkTo(btn_done.top)
                        end.linkTo(parent.end)
                    },
                color = colorResource(id = R.color.green_60),
                fontSize = 150.sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "%s",
                modifier = Modifier
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .padding(2.dp)
                    .constrainAs(phase_dialogue) {
                        top.linkTo(exercise_progress.bottom)
                        start.linkTo(parent.start)
                    },
                color = colorResource(id = R.color.white),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
            Image(
                modifier = Modifier
                    .padding(top = 10.dp, end = 10.dp)
                    .width(30.dp)
                    .height(30.dp)
                    .padding(2.dp)
                    .constrainAs(btn_gif_display) {
                        top.linkTo(exercise_progress.bottom)
                        end.linkTo(parent.end)
                    }, painter = painterResource(id = R.drawable.ic_guideline),
                contentDescription = "Instructions"
            )
            Image(
                modifier = Modifier
                    .width(150.dp)
                    .height(150.dp)
                    .constrainAs(pause_indicator) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, painter = painterResource(id = R.drawable.ic_pause_video),
                contentDescription = "Pause"
            )
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.blue)),
                modifier = Modifier
                    .padding(25.dp)
                    .height(55.dp)
                    .width(90.dp)
                    .constrainAs(btn_done) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(btn_container.end)
                    },
            ) {
                Text(text = "I'm Done")
            }
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.blue)),
                modifier = Modifier
                    .padding(start = 40.dp, bottom = 25.dp)
                    .height(55.dp)
                    .width(90.dp)
                    .constrainAs(btn_container) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(btn_done.start)
                        end.linkTo(parent.end)
                    },
            ) {
                Text(text = "Pause")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ComposableExerciseScreenPreview() {
    EmmaVirtualTherapistTheme {
        ComposableExerciseScreen()
    }
}


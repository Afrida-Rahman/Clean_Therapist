package org.mmh.clean_therapist.android.feature_exercise.presentation.exerciseList.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.mmh.clean_therapist.android.core.component.OutlineInputTextField

@Composable
fun ManualTrackingForm(
    exerciseName: String,
    repetitionField: State<String>,
    onRepetitionValueChanged: (value: String) -> Unit,
    setField: State<String>,
    onSetValueChanged: (value: String) -> Unit,
    wrongField: State<String>,
    onWrongValueChanged: (value: String) -> Unit,
    onCloseClicked: () -> Unit,
    onSaveDataClick: () -> Unit,
    saveDataButtonClickState: State<Boolean>
) {
    Dialog(onDismissRequest = { onCloseClicked() }) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = exerciseName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(1f)
                    )
                    IconButton(
                        onClick = { onCloseClicked() },
                        modifier = Modifier
                            .size(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Icon")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlineInputTextField(
                    field = repetitionField,
                    onValueChange = { onRepetitionValueChanged(it) },
                    placeholder = "Repetition Count",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlineInputTextField(
                    field = setField,
                    onValueChange = { onSetValueChanged(it) },
                    placeholder = "Set Count",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlineInputTextField(
                    field = wrongField,
                    onValueChange = { onWrongValueChanged(it) },
                    placeholder = "Wrong Count",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onSaveDataClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    if (saveDataButtonClickState.value) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = "Save Data",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
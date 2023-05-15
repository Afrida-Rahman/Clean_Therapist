package org.mmh.clean_therapist.android.feature_exercise.presentation.guideline.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InstructionSection(instruction: String) {
    val htmlTagRegex = Regex("<[^>]*>|&nbsp|;")
    val cleanInstruction = htmlTagRegex.replace(instruction, "").replace("\n", " ")
    Column(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Text(text = "Exercise Instruction", style = MaterialTheme.typography.h2)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = cleanInstruction)
    }
}
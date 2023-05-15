package org.mmh.clean_therapist.android.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme

@Composable
fun OutlineInputTextField(
    field: State<String>,
    placeholder: String,
    label: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    onValueChange: (value: String) -> Unit = {},
    @DrawableRes leadingIcon: Int? = null,
    @DrawableRes trailingIcon: Int? = null,
    onIconPressed: () -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isEnable: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val iconSize = 20.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        label?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
        }
        if (leadingIcon != null) {
            OutlinedTextField(
                value = field.value,
                onValueChange = {
                    onValueChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = leadingIcon),
                        contentDescription = "Leading Icon",
                        modifier = Modifier.size(iconSize)
                    )
                },
                trailingIcon = {
                    trailingIcon?.let {
                        IconButton(
                            onClick = {
                                onIconPressed()
                            },
                            enabled = isEnable
                        ) {
                            Icon(
                                painter = painterResource(id = trailingIcon),
                                contentDescription = "",
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    }
                },
                singleLine = true,
                placeholder = { Text(placeholder) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyboardType,
                    imeAction = imeAction
                ),
                visualTransformation = visualTransformation,
                enabled = isEnable,
                shape = MaterialTheme.shapes.medium,
                keyboardActions = keyboardActions
            )
        } else {
            OutlinedTextField(
                value = field.value,
                onValueChange = {
                    onValueChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    trailingIcon?.let {
                        IconButton(
                            onClick = {
                                onIconPressed()
                            },
                            enabled = isEnable
                        ) {
                            Icon(
                                painter = painterResource(id = trailingIcon),
                                contentDescription = "",
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                placeholder = { Text(placeholder) },
                label = { Text(placeholder) },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = visualTransformation,
                enabled = isEnable,
                shape = MaterialTheme.shapes.medium
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun OutlineInputTextFieldPreview() {
    val field = remember {
        mutableStateOf("")
    }
    EmmaVirtualTherapistTheme {
        OutlineInputTextField(
            label = "First name",
            field = field,
            onValueChange = {},
            placeholder = "Text field",
            keyboardType = KeyboardType.Text,
            leadingIcon = R.drawable.search
        )
    }
}
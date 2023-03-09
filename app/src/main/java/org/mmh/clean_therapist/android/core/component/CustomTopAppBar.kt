package org.mmh.clean_therapist.android.core.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.mmh.clean_therapist.android.ui.theme.EmmaVirtualTherapistTheme
import org.mmh.clean_therapist.R

@Composable
fun CustomTopAppBar(
    @DrawableRes leadingIcon: Int,
    onClickLeadingIcon: () -> Unit = {},
    @DrawableRes trailingIcon: Int? = null,
    extraContent: @Composable () -> Unit = {},
    onClickTrailingIcon: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = { onClickLeadingIcon() }) {
                Icon(
                    painter = painterResource(id = leadingIcon),
                    contentDescription = "Leading Icon",
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = CircleShape
                        )
                        .size(40.dp)
                        .padding(12.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
            Box(modifier = Modifier.size(40.dp)) {
                trailingIcon?.let {
                    IconButton(onClick = { onClickTrailingIcon() }) {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = "Trailing Icon",
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = Color.LightGray,
                                    shape = CircleShape
                                )
                                .fillMaxSize()
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
        extraContent()
    }
}

@Preview(showBackground = true)
@Composable
fun CustomTopAppBarPreview() {
    EmmaVirtualTherapistTheme {
        CustomTopAppBar(
            leadingIcon = R.drawable.menu_new,
            trailingIcon = R.drawable.filter,
            extraContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .background(Color.White)
                ) {
                    OutlineInputTextField(
                        field = remember { mutableStateOf("") },
                        placeholder = "Test ID",
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Body Region",
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.Bold
                    )
                    OutlineInputTextField(
                        field = remember { mutableStateOf("") },
                        placeholder = "Test ID"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryLargeButton(text = "Apply")
                }
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.mmh),
                contentDescription = "MyMedicalHub",
                modifier = Modifier
                    .height(50.dp)
            )
        }
    }
}
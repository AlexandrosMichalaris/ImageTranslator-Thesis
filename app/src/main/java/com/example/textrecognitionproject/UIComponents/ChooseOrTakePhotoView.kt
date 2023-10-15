package com.example.textrecognitionproject.UIComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ChooseOrTakePhotoView(
    choosePhotoFromLibraryAction: () -> Unit,
    takePhotoAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)

    ) {
        Box(
            modifier = Modifier
                .weight(2f)
                .wrapContentSize()
        ) {
            Button(
                onClick = { choosePhotoFromLibraryAction() },
            ) {
                Text(
                    text = "Pick Image From Gallery",
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize()

        ) {
            Button(
                modifier = Modifier.fillMaxHeight(),
                onClick = { takePhotoAction() },
            ) {
                Text(
                    text = "Take Photo",
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}
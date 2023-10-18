package com.example.textrecognitionproject.UIComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ChooseOrTakePhotoView(
    choosePhotoFromLibraryAction: () -> Unit,
    takePhotoAction: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 16.dp)
            .padding(horizontal = 10.dp)

    ) {

        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { choosePhotoFromLibraryAction() },
        ) {
            Text(
                text = "Pick Image \nFrom Gallery",
                textAlign = TextAlign.Center
            )
        }


        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { takePhotoAction() },
        ) {
            Text(
                text = "Take Photo",
                textAlign = TextAlign.Center
            )
        }

        Button(
            modifier = Modifier.fillMaxHeight(),
            onClick = { takePhotoAction() },
        ) {
            Text(
                text = "History",
                textAlign = TextAlign.Center
            )
        }

    }

}
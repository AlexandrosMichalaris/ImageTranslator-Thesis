package com.example.textrecognitionproject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TakePhotoButton(
    onRequestPermission: () -> Unit
) {
    TakePhotoButtonContent(
        onRequestPermission = onRequestPermission
    )
}

@Composable
private fun TakePhotoButtonContent(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onRequestPermission) {
            Text(text = "Take Photo")
        }
    }
}

@Preview
@Composable
private fun Preview_NoPermissionContent() {
    TakePhotoButtonContent(
        onRequestPermission = {}
    )
}
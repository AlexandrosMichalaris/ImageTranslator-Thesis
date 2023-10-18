package com.example.textrecognitionproject.UIComponents

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.moyuru.cropify.Cropify
import io.moyuru.cropify.rememberCropifyState
import java.io.InputStream

@Composable
fun CropImageView(
    bitmap: Bitmap,
    bitmapResult: (Bitmap) -> Unit
) {
    var bitmap by remember { mutableStateOf(bitmap) }
    val state = rememberCropifyState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        Cropify(
            bitmap = bitmap.asImageBitmap(),
            state = state,
            onImageCropped = {
                bitmapResult(it.asAndroidBitmap())
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f)
                .padding(horizontal = 10.dp)
                .padding(vertical = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = {
                    bitmap = rotateBitmap(
                        bitmap,
                        degrees = -90f
                    )
                }) {
                Text(
                    text = "Rotate\nLeft",
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = {
                    state.crop()
                }) {
                Text(
                    text = "Crop",
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = {
                    bitmapResult(bitmap)
                }) {
                Text(
                    text = "Don't\nCrop",
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                onClick = {
                    bitmap = rotateBitmap(
                        bitmap!!,
                        degrees = 90f
                    )
                }
            ) {
                Text(
                    text = "Rotate\nRight",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Function to convert a Uri to a Bitmap
fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

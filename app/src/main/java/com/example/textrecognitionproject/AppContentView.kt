
package com.example.textrecognitionproject

import CameraView
import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppContent(context: Context) {
    var shouldShowCamera by remember { mutableStateOf<Boolean>(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var inputImage: InputImage? by remember { mutableStateOf(null) }
    val cameraPermissionState: PermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uriList ->
            if (uriList != null) {
                selectedImageUri = uriList
            }

            // Create an InputImage from the selected URI
            inputImage = uriList?.let { createInputImageFromUri(context, it) }
        }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChooseOrTakePhotoView(
                choosePhotoFromLibraryAction = { galleryLauncher.launch("image/*") },
                takePhotoAction = { takePhotoAction(state = cameraPermissionState) { shouldShowCamera = it} }
            )
            if (selectedImageUri != null) {
                Image(
                    painter = rememberImagePainter(selectedImageUri),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .size(200.dp)
                )
            }
        }
        if (shouldShowCamera) {
            CameraView(outputDirectory = getOutputDirectory(context = context),
                executor = Executors.newSingleThreadExecutor(),
                onImageCaptured = {
                    selectedImageUri = it
                    shouldShowCamera = false
                },
                onError = { Log.e("", "View error:", it) }
            )
           }
    }



}


private fun createInputImageFromUri(
    context: Context,
    uri: Uri
): InputImage? {
    var image: InputImage? = null
    try {
        image = InputImage.fromFilePath(context, uri)
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return image
}

private fun getOutputDirectory(context: Context): File {
    val externalDirs = context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)
    for (fileDir in externalDirs) {
        if (fileDir != null) {
            val mediaDir = File(fileDir, context.resources.getString(R.string.app_name)).apply { mkdirs() }
            if (mediaDir.exists()) {
                return mediaDir
            }
        }
    }

    // If no suitable external directory is found, fallback to using the app's internal storage
    return context.filesDir
}

@OptIn(ExperimentalPermissionsApi::class)
fun takePhotoAction(
    state: PermissionState,
    showCamera: (Boolean) -> Unit
) {
    if (!state.status.isGranted) {
        state.launchPermissionRequest()
    } else {
        showCamera(true)
    }
}
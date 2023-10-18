package com.example.textrecognitionproject.mainViews

import CameraView
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.textrecognitionproject.AppContentViewModel
import com.example.textrecognitionproject.LanguageModel
import com.example.textrecognitionproject.R
import com.example.textrecognitionproject.UIComponents.ChooseOrTakePhotoView
import com.example.textrecognitionproject.UIComponents.CropImageView
import com.example.textrecognitionproject.UIComponents.CustomDropdown
import com.example.textrecognitionproject.UIComponents.uriToBitmap
import com.example.textrecognitionproject.enums.MLKitStatus
import com.example.textrecognitionproject.ui.theme.LightBlue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import java.io.File
import java.util.concurrent.Executors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppContent(viewModel: AppContentViewModel) {
    val localContext = LocalContext.current
    val extractedTextFromMLKitImage by viewModel.textFromML.collectAsState()
    val languageFromTextFromML by viewModel.languageFromTextFromML.collectAsState()
    val mlKitStatus by viewModel.textImageProcessingStatus.collectAsState()
    val selectedLanguageToTranslateTo by viewModel.selectedLanguageToTranslateTo.collectAsState()
    val languageRecognitionStatus by viewModel.languageIdentificationProcessingStatus.collectAsState()
    val translationStatus by viewModel.textImageTranslationStatus.collectAsState()
    val translatedText by viewModel.translatedText.collectAsState()
    var shouldShowCamera by remember { mutableStateOf<Boolean>(false) }
    var shouldShowImageCropper by remember { mutableStateOf(false) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var inputImage: InputImage? by remember { mutableStateOf(null) }

    val cameraPermissionState: PermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                bitmap = uriToBitmap(localContext, uri)
                shouldShowImageCropper = true
            }
        }

    BackHandler(enabled = shouldShowCamera) {
        shouldShowCamera = false
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChooseOrTakePhotoView(
                choosePhotoFromLibraryAction = { galleryLauncher.launch("image/*") },
                takePhotoAction = {
                    takePhotoAction(state = cameraPermissionState) {
                        shouldShowCamera = it
                    }
                },
            )
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp, 8.dp)
                        .size(150.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                when (mlKitStatus) {
                    MLKitStatus.IN_PROGRESS -> CircularProgressIndicator()
                    else -> {
                        ExtractedTextView(
                            text = extractedTextFromMLKitImage,
                            mlKitStatus = mlKitStatus,
                            failedText = "Failed to extract text."
                        )
                        if (languageFromTextFromML != null && languageRecognitionStatus == MLKitStatus.SUCCESS) {
                            LanguagePicker(
                                currentLanguageOfText = languageFromTextFromML!!.displayLocale,
                                languageList = viewModel.languageList,
                                selectedLanguageToTranslateTo = {
                                    viewModel.setSelectedLanguageToTranslateTo(it)
                                }
                            )
                            Row {
                                Button(
                                    enabled = (selectedLanguageToTranslateTo != null),
                                    onClick = { viewModel.translateText() }) {
                                    Text(text = "Translate")
                                }
                            }

                            when (translationStatus) {
                                MLKitStatus.IN_PROGRESS -> CircularProgressIndicator()
                                else -> {
                                    if (translatedText.isNotEmpty() && translationStatus == MLKitStatus.SUCCESS) {
                                        ExtractedTextView(
                                            text = translatedText,
                                            mlKitStatus = translationStatus,
                                            failedText = "Failed to translate"
                                        )
                                    }
                                }
                            }
                        } else if (languageRecognitionStatus == MLKitStatus.FAILED) {
                            Text(text = "Cannot Identify Language")
                        }
                    }
                }

            }
        }
        if (shouldShowCamera) {
            CameraView(outputDirectory = getOutputDirectory(context = localContext),
                executor = Executors.newSingleThreadExecutor(),
                onImageCaptured = { it ->
                    bitmap = it
                    shouldShowCamera = false
                    shouldShowImageCropper = true
                },
                onError = { Log.e("", "View error:", it) }
            )
        }
        if (shouldShowImageCropper && bitmap != null) {
            CropImageView(
                bitmap = bitmap!!,
                bitmapResult = {
                    bitmap = it
                    if (bitmap != null) {
                        inputImage = InputImage.fromBitmap(bitmap!!, 0)
                    }
                    inputImage.let {
                        if (it != null) {
                            viewModel.inputImageProcessingWithMLKIT(inputImage = it)
                        }
                    }
                    shouldShowImageCropper = false
                }
            )
        }
    }

}

private fun getOutputDirectory(context: Context): File {
    val externalDirs = context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)
    for (fileDir in externalDirs) {
        if (fileDir != null) {
            val mediaDir =
                File(fileDir, context.resources.getString(R.string.app_name)).apply { mkdirs() }
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

@Composable
fun ExtractedTextView(
    text: String,
    mlKitStatus: MLKitStatus,
    failedText: String
) {
    // Generate a list of items
    val scrollState = rememberScrollState()
    val clipboardManager: androidx.compose.ui.platform.ClipboardManager =
        LocalClipboardManager.current
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(8.dp), clip = true)
            .background(color = LightBlue, shape = RoundedCornerShape(8.dp))
            .padding(10.dp)

    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        ) {
            val getFinalText: String = when (mlKitStatus) {
                MLKitStatus.FAILED -> failedText
                MLKitStatus.SUCCESS -> text
                else -> {
                    "Cannot retrieve status, try again."
                }
            }
            Text(
                text = getFinalText,
                modifier = Modifier
                    .heightIn(max = 100.dp)
                    .verticalScroll(scrollState)
            )
            if (mlKitStatus == MLKitStatus.SUCCESS) {
                Text(
                    text = "COPY",
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .clickable(onClick = { clipboardManager.setText(AnnotatedString((text))) }),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LanguagePicker(
    currentLanguageOfText: String,
    languageList: MutableList<LanguageModel>,
    selectedLanguageToTranslateTo: (String) -> (Unit)
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val configuration = LocalConfiguration.current
        val listOptions = languageList.map { it.displayLocale }
        CustomDropdown(
            modifier = Modifier
                .width(configuration.screenWidthDp.dp / 3)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ),
            modifierInsideItems = Modifier
                .width(configuration.screenWidthDp.dp / 3)
                .heightIn(max = 200.dp),
            listOptions = listOf(),
            onSelection = {

            },
            value = currentLanguageOfText
        )
        Text(
            modifier = Modifier.align(CenterVertically),
            text = "To ->"
        )
        CustomDropdown(
            modifier = Modifier
                .width(configuration.screenWidthDp.dp / 3)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                ),
            modifierInsideItems = Modifier
                .width(configuration.screenWidthDp.dp / 3)
                .heightIn(max = 150.dp),
            listOptions = listOptions,
            onSelection = {
                selectedLanguageToTranslateTo(it)
            },
            value = "Select"
        )
    }
}



package com.example.textrecognitionproject

import AppContentViewModel
import CameraView
import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
fun AppContent(navController: NavController) {
    val localContext = LocalContext.current
    val viewModel: AppContentViewModel = viewModel()
    val stringList by viewModel.textFromML.collectAsState()
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
            inputImage = uriList?.let { createInputImageFromUri(localContext, it) }
            inputImage.let {
                if (it != null) {
                    viewModel.inputImageProcessingWithMLKIT(inputImage = it)
                }
            }
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
            EspdTextInputWithDropdownFiltering(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onSelection = {},
                label = "label",
                value = "value",
                onError = {}
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
            LazyColumnExample(test = stringList)

        }
        if (shouldShowCamera) {
            CameraView(outputDirectory = getOutputDirectory(context = localContext),
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

@Composable
fun LazyColumnExample(test: List<String>) {
    // Generate a list of items
    val itemss = (1..100).toList()

    // Remember the current scroll position
    var scrollState by remember { mutableStateOf(0) }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberLazyListState(initialFirstVisibleItemIndex = scrollState)
    ) {
        items(test) { item ->
            // Composable for each item in the list
            Text(
                text = "Item $item",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)

            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Text field to simulate scrolling to a specific item
    BasicTextField(
        value = scrollState.toString(),
        onValueChange = {
            val newPosition = it.toIntOrNull() ?: 0
            scrollState = newPosition
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EspdTextInputWithDropdownFiltering(
    modifier: Modifier = Modifier,
    listOptions: List<String>? = listOf(
        "Option 1",
        "Option 2",
        "Option 3",
        "Option 4",
        "Option 5"
    ), // TODO use a list of objects and return an id on selection
    onSelection: (String) -> Unit,
    label: String,
    value: String,
    onError: (Boolean) -> Unit = {},
    errorMessage: String? = null,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(value) }
// filter options based on text field value
    val filteringOptions by remember {
        derivedStateOf {
            listOptions?.filter { it.contains(textFieldValue, ignoreCase = true) }
        }
    }
    val errorMessage by remember {
        derivedStateOf {
            if (listOptions?.any { it == textFieldValue } == true || textFieldValue.isEmpty()) ""
            else errorMessage ?: "R.string.text_must_match_one_of_the_options"
        }
    }
    LaunchedEffect(errorMessage) {
        onError(errorMessage.isNotEmpty())
    }
    Column(Modifier.animateContentSize(animationSpec = tween())) {
        ExposedDropdownMenuBox(
            modifier = modifier,
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
// The menuAnchor modifier must be passed to the text field for correctness.
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onSelection(it)
                },
                label = { Text(label) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                isError = errorMessage.isNotEmpty(),
                supportingText = if (errorMessage.isNotEmpty()) {
                    {
                        Text(
                            text = errorMessage,
                            style = TextStyle(color = MaterialTheme.colorScheme.error)
                        )
                    }
                } else null,
            )


            if (filteringOptions?.isNotEmpty() == true) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth().heightIn(max = 100.dp)
                ) {
                    filteringOptions?.forEach { selectionOption ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(selectionOption) },
                            onClick = {
                                textFieldValue = selectionOption
                                expanded = false
                                onSelection(selectionOption)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}

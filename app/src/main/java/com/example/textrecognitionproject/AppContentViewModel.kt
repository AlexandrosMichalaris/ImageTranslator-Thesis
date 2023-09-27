import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.common.InputImage

class AppContentViewModel : ViewModel() {
    private val _selectedImageUri = MutableLiveData<Uri?>(null)
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri

    private val _inputImage = MutableLiveData<InputImage?>(null)
    val inputImage: LiveData<InputImage?> = _inputImage

    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }

    fun setInputImage(inputImage: InputImage?) {
        _inputImage.value = inputImage
    }
}
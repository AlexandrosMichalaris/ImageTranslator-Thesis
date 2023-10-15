package com.example.textrecognitionproject

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.textrecognitionproject.enums.MLKitStatus.FAILED
import com.example.textrecognitionproject.enums.MLKitStatus.IN_PROGRESS
import com.example.textrecognitionproject.enums.MLKitStatus.NOT_STARTED_YET
import com.example.textrecognitionproject.enums.MLKitStatus.SUCCESS
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale

class AppContentViewModel : ViewModel() {
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _textFromML = MutableStateFlow<String>("")
    val textFromML: MutableStateFlow<String> = _textFromML
    private val _translatedText = MutableStateFlow<String>("")
    val translatedText: MutableStateFlow<String> = _translatedText
    private var _translationModels = mutableSetOf<TranslateRemoteModel>()

    val isTranslatorModelDownloaded = MutableStateFlow<Boolean>(false)
    private var _isTranslatorModelDownloaded : MutableStateFlow<Boolean> = isTranslatorModelDownloaded

    var languageList = mutableListOf<LanguageModel>()
    private val _languageFromTextFromML: MutableStateFlow<LanguageModel?> = MutableStateFlow(null)
    val languageFromTextFromML: MutableStateFlow<LanguageModel?> = _languageFromTextFromML
    private val _selectedLanguageToTranslateTo = MutableStateFlow<LanguageModel?>(null)
    val selectedLanguageToTranslateTo: MutableStateFlow<LanguageModel?> = _selectedLanguageToTranslateTo
    private val _textImageProcessingStatus = MutableStateFlow(NOT_STARTED_YET)
    val textImageProcessingStatus = _textImageProcessingStatus
    private val _languageIdentificationProcessingStatus = MutableStateFlow(NOT_STARTED_YET)
    val languageIdentificationProcessingStatus = _languageIdentificationProcessingStatus
    private val _textImageTranslationStatus = MutableStateFlow(NOT_STARTED_YET)
    val textImageTranslationStatus = _textImageTranslationStatus

    init {
        getTranslatorModels()
    }
    fun inputImageProcessingWithMLKIT(inputImage: InputImage) {
        resetValues()

        this._textImageProcessingStatus.value = IN_PROGRESS
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                processResultText(visionText = visionText)
            }
            .addOnFailureListener { e ->
                this._textImageProcessingStatus.value = FAILED
            }
    }

    private fun processResultText(visionText: Text) {
        _textFromML.value = visionText.text
        if (visionText.text.isEmpty()) {
            this._textImageProcessingStatus.value = FAILED
        } else {
            this._textImageProcessingStatus.value = SUCCESS
        }
        identifyTextLanguage(text = visionText.text)
    }

    private fun identifyTextLanguage(text: String) {
        val languageIdentifier = LanguageIdentification.getClient()
        this._languageIdentificationProcessingStatus.value = IN_PROGRESS
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.i("LanguageIdentification", "Can't identify language.")
                    this._languageIdentificationProcessingStatus.value = FAILED
                } else {
                    Log.i("LanguageIdentification", "Language: $languageCode")
                    val currentLocale = Locale(languageCode)
                    this._languageFromTextFromML.value = LanguageModel(locale = currentLocale, displayLocale = currentLocale.displayLanguage)
                    getAllTranslateLanguages()
                    this._languageIdentificationProcessingStatus.value = SUCCESS
                }
            }
            .addOnFailureListener {
                this._languageIdentificationProcessingStatus.value = FAILED
            }
    }

    private fun getAllTranslateLanguages() {
        for (x in TranslateLanguage.getAllLanguages()) {
            val locale = Locale(x)
            languageList.add(LanguageModel(locale = locale, displayLocale = locale.displayLanguage))
            Log.i("LanguageTranslatorOptionsBuilder", locale.displayLanguage)
        }
    }

    fun setSelectedLanguageToTranslateTo(language: String) {
        this._selectedLanguageToTranslateTo.value = languageList.find { it.displayLocale == language }

    }

    private fun resetValues() {
        _textFromML.value = ""
        _selectedLanguageToTranslateTo.value = null
        _translatedText.value = ""
        _languageFromTextFromML.value = null
        _textImageProcessingStatus.value = NOT_STARTED_YET
        _languageIdentificationProcessingStatus.value = NOT_STARTED_YET
        _textImageTranslationStatus.value = NOT_STARTED_YET
    }

    fun translateText() {
        _textImageTranslationStatus.value = IN_PROGRESS
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(_languageFromTextFromML.value?.locale.toString())
            .setTargetLanguage(_selectedLanguageToTranslateTo.value?.locale.toString())
            .build()
        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder()
            .requireWifi()
            .build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                translator.translate(_textFromML.value)
                    .addOnSuccessListener { translatedText ->
                        this._translatedText.value = translatedText.toString()
                        _textImageTranslationStatus.value = SUCCESS
                    }
                    .addOnFailureListener { exception ->
                        _textImageTranslationStatus.value = FAILED
                    }
            }
            .addOnFailureListener { exception ->
                exception.cause?.message
                _textImageTranslationStatus.value = FAILED

            }
    }

    fun downloadModel() {

    }

    fun deleteModel() {

    }

    fun isModelAlreadyDownloaded(): Boolean {
        return true
    }

    private fun getTranslatorModels() {
        val modelManager = RemoteModelManager.getInstance()

        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                this._translationModels = models
                for (model in models) {
                    Log.d("translator", "model: ${model.language}")
                }
            }
            .addOnFailureListener {
                // Error.
            }

    }
}
data class LanguageModel(val locale: Locale, val displayLocale: String)
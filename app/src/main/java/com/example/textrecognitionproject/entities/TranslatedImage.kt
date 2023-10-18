package com.example.textrecognitionproject.entities

import android.graphics.Bitmap
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.io.ByteArrayOutputStream

class TranslatedImage() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var bitmapByteArray: ByteArray? = null
    var translatedText: String = ""
    constructor(bitmap: Bitmap, translatedText: String) : this() {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val imageByteArray = stream.toByteArray()
        this.bitmapByteArray = imageByteArray
        this.translatedText = translatedText
    }
}
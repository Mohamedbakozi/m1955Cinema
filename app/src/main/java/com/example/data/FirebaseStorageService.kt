package com.example.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

sealed class ImageUploadState {
    object Idle : ImageUploadState()
    data class Uploading(val progressPercent: Int) : ImageUploadState()
    data class Success(val downloadUrl: String) : ImageUploadState()
    data class Error(val message: String) : ImageUploadState()
}

class FirebaseStorageService {
    fun uploadPosterImage(imageUri: Uri, context: Context): Flow<ImageUploadState> {
        return flowOf(ImageUploadState.Success(imageUri.toString()))
    }
}


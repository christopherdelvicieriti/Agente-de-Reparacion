package com.delvicier.fixagent.ui.screens.setup

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.model.SetupRequest
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

data class SetupUiState(
    val hasToken: Boolean = false,

    val user: String = "",
    val pass: String = "",

    val secretKey: String? = null,
    val isContinueEnabled: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SetupAccountViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val token = preferencesRepository.tokenAccount.first()
            if (!token.isNullOrEmpty()) {
                _uiState.update { it.copy(hasToken = true) }
            }
        }
    }


    fun onQrDetected(token: String) {
        if (_uiState.value.hasToken || _uiState.value.isLoading) return

        viewModelScope.launch {
            if (token.isNotEmpty() && token.length > 5) {
                preferencesRepository.saveTokenAccount(token)
                _uiState.update { it.copy(hasToken = true, errorMessage = null) }
            }
        }
    }

    fun resetToken() {
        viewModelScope.launch {
            preferencesRepository.clearTokenAccount()
            _uiState.update { SetupUiState(hasToken = false) }
        }
    }


    fun onUserChanged(text: String) { _uiState.update { it.copy(user = text) } }
    fun onPassChanged(text: String) { _uiState.update { it.copy(pass = text) } }

    fun onCreateUser() {
        val user = _uiState.value.user.trim()
        val pass = _uiState.value.pass.trim()

        if (user.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Complete todos los campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val qrToken = preferencesRepository.tokenAccount.first()

                if (qrToken.isNullOrEmpty()) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: Token QR perdido. Escanee de nuevo.") }
                    return@launch
                }

                val request = SetupRequest(user, pass)

                val response = apiService.setupAccount("Bearer $qrToken", request)

                if (response.isSuccessful && response.body() != null) {
                    val secretKey = response.body()!!.secret_key

                    preferencesRepository.saveSecretKey(secretKey)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            secretKey = secretKey,
                            isContinueEnabled = false
                        )
                    }
                } else {
                    val errorMsg = "Error (${response.code()}): ${response.message()}"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun onTextCopied() {
        markBackupAsDone()
    }

    fun onQrDownloaded(context: Context, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = saveBitmapToGallery(context, bitmap)

            viewModelScope.launch(Dispatchers.Main) {
                if (success) {
                    Toast.makeText(context, "QR guardado en Galería", Toast.LENGTH_LONG).show()
                    markBackupAsDone()
                } else {
                    Toast.makeText(context, "Error al guardar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun markBackupAsDone() {
        viewModelScope.launch {
            preferencesRepository.confirmBackupDone()

            _uiState.update { it.copy(isContinueEnabled = true) }
        }
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
        val filename = "FixAgent_Secret_${System.currentTimeMillis()}.png"
        val fos: OutputStream?

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                fos = FileOutputStream(image)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
package com.delvicier.fixagent.ui.screens.recover

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.model.RecoverRequest
import com.delvicier.fixagent.data.model.ResetPasswordRequest
import com.delvicier.fixagent.data.network.ApiService
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream

data class RecoverUiState(

    val secretKey: String = "",
    val resetToken: String? = null,

    val newPass: String = "",
    val confirmPass: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RecoverViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoverUiState())
    val uiState = _uiState.asStateFlow()

    fun onImageSelected(uri: Uri, context: Context) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo leer la imagen") }
                    return@launch
                }

                val intArray = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

                val result = MultiFormatReader().decode(binaryBitmap)
                val secretKey = result.text

                verifySecretKey(secretKey)

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se encontró un código QR válido en la imagen.") }
            }
        }
    }

    private suspend fun verifySecretKey(secretKey: String) {
        try {
            val request = RecoverRequest(secretKey)
            val response = apiService.recoverPassword(request)

            if (response.isSuccessful && response.body() != null) {
                val resetToken = response.body()!!.resetToken

                _uiState.update { it.copy(isLoading = false, resetToken = resetToken) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Clave secreta incorrecta o inválida.") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
        }
    }

    fun onNewPassChange(text: String) { _uiState.update { it.copy(newPass = text) } }
    fun onConfirmPassChange(text: String) { _uiState.update { it.copy(confirmPass = text) } }

    fun resetPassword() {
        val state = _uiState.value

        if (state.newPass.isBlank() || state.confirmPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Complete todos los campos") }
            return
        }
        if (state.newPass != state.confirmPass) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ResetPasswordRequest(state.newPass)

                val response = apiService.resetPassword("Bearer ${state.resetToken}", request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al restablecer (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }
}
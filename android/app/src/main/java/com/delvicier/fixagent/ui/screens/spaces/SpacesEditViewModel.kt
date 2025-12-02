package com.delvicier.fixagent.ui.screens.spaces

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.model.SpaceRequest
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

data class SpaceEditUiState(
    val id: Int = 0,
    val alias: String = "",
    val description: String = "",
    val color: String = "#33FF57",

    val imagePath: String? = null,
    val fullImageUrl: String? = null,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class SpaceEditViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpaceEditUiState())
    val uiState = _uiState.asStateFlow()

    fun loadSpace(id: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val baseUrl = preferencesRepository.baseUrl.first()?.removeSuffix("/") ?: ""

                val response = apiService.getSpaceById(id)

                if (response.isSuccessful && response.body() != null) {
                    val space = response.body()!!

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            id = space.id,
                            alias = space.alias,
                            description = space.description,
                            color = space.color,
                            imagePath = space.image,
                            fullImageUrl = if (!space.image.isNullOrEmpty()) "$baseUrl/${space.image}" else null
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo cargar la información del espacio.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun onAliasChange(text: String) { _uiState.update { it.copy(alias = text) } }
    fun onDescriptionChange(text: String) { _uiState.update { it.copy(description = text) } }
    fun onColorChange(hex: String) { _uiState.update { it.copy(color = hex) } }

    fun updateSpace() {
        val state = _uiState.value

        if (state.alias.isBlank() || state.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre y la descripción son obligatorios") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = SpaceRequest(
                    alias = state.alias,
                    descripcion = state.description,
                    image = state.imagePath ?: "",
                    color = state.color
                )

                val response = apiService.updateSpace(state.id, request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun uploadNewImage(uri: Uri, context: Context) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val baseUrl = preferencesRepository.baseUrl.first()?.removeSuffix("/") ?: ""
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(uri)
                    .size(1280, 720)
                    .allowHardware(false)
                    .build()

                val result = imageLoader.execute(request)

                if (result is SuccessResult) {
                    val bitmap = (result.drawable as BitmapDrawable).bitmap
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                    val byteArray = outputStream.toByteArray()

                    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, byteArray.size)
                    val multipartBody = MultipartBody.Part.createFormData("file", "update.jpg", requestBody)

                    val response = apiService.uploadImage(multipartBody)

                    if (response.isSuccessful && response.body() != null) {
                        val path = response.body()!!.path
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                imagePath = path,
                                fullImageUrl = "$baseUrl/$path"
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error al subir imagen") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
    fun deleteSpace() {
        val id = _uiState.value.id
        if (id == 0) return

        _uiState.update { it.copy(showDeleteDialog = false, isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteSpace(id)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }

}
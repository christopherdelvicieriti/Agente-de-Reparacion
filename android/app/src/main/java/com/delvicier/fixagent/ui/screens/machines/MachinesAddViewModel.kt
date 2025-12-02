package com.delvicier.fixagent.ui.screens.machines

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
import com.delvicier.fixagent.data.model.CreateMachineRequest
import com.delvicier.fixagent.data.model.Space
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

enum class MachineImageType { ANVERSO, REVERSO, ACCESORIOS }

data class MachinesAddUiState(
    val model: String = "",
    val description: String = "",
    val accessories: String = "",
    val cost: String = "",

    val selectedSpaceId: Int? = null,
    val selectedSpace: Space? = null,
    val baseUrl: String = "",

    val imgAnversoPath: String? = null,
    val imgReversoPath: String? = null,
    val imgAccessoriesPath: String? = null,
    val imgAnversoUrl: String? = null,
    val imgReversoUrl: String? = null,
    val imgAccessoriesUrl: String? = null,

    val availableSpaces: List<Space> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class MachinesAddViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MachinesAddUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSpaces()
    }

    private fun loadSpaces() {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val baseUrl = preferencesRepository.baseUrl.first()?.removeSuffix("/") ?: ""

                val response = apiService.getSpaces()

                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            availableSpaces = response.body()!!,
                            baseUrl = baseUrl
                        )
                    }
                }
            } catch (e: Exception) {
                // Error silencioso
            }
        }
    }

    fun onModelChange(text: String) { _uiState.update { it.copy(model = text) } }
    fun onDescriptionChange(text: String) { _uiState.update { it.copy(description = text) } }
    fun onAccessoriesChange(text: String) { _uiState.update { it.copy(accessories = text) } }
    fun onCostChange(text: String) { if (text.matches(Regex("^\\d*\\.?\\d*\$"))) _uiState.update { it.copy(cost = text) } }

    fun onSpaceSelected(id: Int) {
        val space = _uiState.value.availableSpaces.find { it.id == id }
        _uiState.update { it.copy(selectedSpaceId = id, selectedSpace = space) }
    }

    fun uploadMachineImage(uri: Uri, context: Context, type: MachineImageType) {
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
                    val multipartBody = MultipartBody.Part.createFormData("file", "machine_add.jpg", requestBody)

                    val response = apiService.uploadImage(multipartBody)

                    if (response.isSuccessful && response.body() != null) {
                        val path = response.body()!!.path
                        val fullUrl = "$baseUrl/$path"

                        _uiState.update { currentState ->
                            when (type) {
                                MachineImageType.ANVERSO -> currentState.copy(imgAnversoPath = path, imgAnversoUrl = fullUrl)
                                MachineImageType.REVERSO -> currentState.copy(imgReversoPath = path, imgReversoUrl = fullUrl)
                                MachineImageType.ACCESORIOS -> currentState.copy(imgAccessoriesPath = path, imgAccessoriesUrl = fullUrl)
                            }
                        }
                        _uiState.update { it.copy(isLoading = false) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Error subiendo imagen") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun createMachine(orderId: Int) {
        val state = _uiState.value

        if (state.model.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El modelo es obligatorio") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateMachineRequest(
                    orderId = orderId,
                    model = state.model,
                    spaceId = state.selectedSpaceId,
                    repairCost = if (state.cost.isBlank()) null else state.cost.toDouble(),
                    description = if (state.description.isBlank()) null else state.description,
                    accessories = if (state.accessories.isBlank()) null else state.accessories,
                    imgAnverso = state.imgAnversoPath,
                    imgReverso = state.imgReversoPath,
                    imgAccessories = state.imgAccessoriesPath
                )

                val response = apiService.createMachine(request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al crear (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
}
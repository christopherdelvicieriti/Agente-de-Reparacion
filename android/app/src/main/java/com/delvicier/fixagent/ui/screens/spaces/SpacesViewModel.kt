package com.delvicier.fixagent.ui.screens.spaces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.model.Space
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SpacesUiState(
    val spaces: List<Space> = emptyList(),
    val baseUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SpacesViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpacesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSpaces()
    }

    fun loadSpaces() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val currentBaseUrl = preferencesRepository.baseUrl.first()?.removeSuffix("/") ?: ""

                // 2. Llamamos a la API
                val response = apiService.getSpaces()

                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            spaces = response.body()!!,
                            baseUrl = currentBaseUrl
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sin conexión: ${e.message}") }
            }
        }
    }
}
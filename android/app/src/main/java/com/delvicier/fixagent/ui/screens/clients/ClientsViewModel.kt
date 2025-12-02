package com.delvicier.fixagent.ui.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.model.Client
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientsUiState(
    val clients: List<Client> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ClientsViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadClients()
    }

    fun loadClients() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getClients()

                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(isLoading = false, clients = response.body()!!) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar clientes (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sin conexión: ${e.message}") }
            }
        }
    }
}
package com.delvicier.fixagent.ui.screens.machines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.model.Machine
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MachinesUiState(
    val machines: List<Machine> = emptyList(),
    val baseUrl: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class MachinesViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MachinesUiState())
    val uiState = _uiState.asStateFlow()

    fun loadMachinesForOrder(orderId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentBaseUrl = preferencesRepository.baseUrl.first()?.removeSuffix("/") ?: ""

                val response = apiService.getMachinesByOrder(orderId)

                if (response.isSuccessful && response.body() != null) {
                    val orderMachines = response.body()!!

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            machines = orderMachines,
                            baseUrl = currentBaseUrl
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar máquinas (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }
}
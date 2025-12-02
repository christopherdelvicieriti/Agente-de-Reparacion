package com.delvicier.fixagent.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.model.Client
import com.delvicier.fixagent.data.model.CreateOrderRequest
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class OrdersAddUiState(
    val selectedClientId: Int? = null,
    val detail: String = "",
    val deliveryDate: String = "",
    val total: String = "",

    val availableClients: List<Client> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class OrdersAddViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersAddUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadClientsForSelect()
    }

    init {
        refreshClients()
    }
    private fun loadClientsForSelect() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getClients()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(availableClients = response.body()!!) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "No se pudieron cargar los clientes") }
            }
        }
    }

    fun refreshClients() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getClients()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { it.copy(availableClients = response.body()!!) }
                }
            } catch (e: Exception) {
            }
        }
    }
    fun onClientSelected(clientId: Int) { _uiState.update { it.copy(selectedClientId = clientId) } }
    fun onDetailChange(text: String) { _uiState.update { it.copy(detail = text) } }
    fun onTotalChange(text: String) {
        if (text.matches(Regex("^\\d*\\.?\\d*\$"))) _uiState.update { it.copy(total = text) }
    }

    fun onDateSelected(millis: Long?) {
        if (millis != null) {

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val dateString = formatter.format(Date(millis))
            _uiState.update { it.copy(deliveryDate = dateString) }
        }
    }

    fun createOrder() {
        val state = _uiState.value

        if (state.selectedClientId == null) {
            _uiState.update { it.copy(errorMessage = "Debe seleccionar un cliente obligatoriamente.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = CreateOrderRequest(
                    clientId = state.selectedClientId,
                    detail = if (state.detail.isBlank()) null else state.detail,
                    timeExtension = null,
                    deliveryDate = if (state.deliveryDate.isBlank()) null else state.deliveryDate,
                    total = if (state.total.isBlank()) null else state.total.toDouble(),
                    isPaid = false,
                    isCompleted = false
                )

                val response = apiService.createOrder(request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al crear orden (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
}
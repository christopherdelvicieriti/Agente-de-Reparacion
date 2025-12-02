package com.delvicier.fixagent.ui.screens.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.model.Client
import com.delvicier.fixagent.data.model.Order
import com.delvicier.fixagent.data.model.UpdateOrderRequest
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

data class OrderEditUiState(
    val orderId: Int = 0,
    val originalOrder: Order? = null,

    val selectedClientId: Int? = null,
    val detail: String = "",
    val timeExtension: String = "",
    val deliveryDate: String = "",
    val total: String = "",
    val isPaid: Boolean = false,

    val isCompleted: Boolean = false,

    val availableClients: List<Client> = emptyList(),

    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class OrdersEditViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderEditUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(orderId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, orderId = orderId) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val clientsResponse = apiService.getClients()
                val clientsList = if (clientsResponse.isSuccessful) clientsResponse.body() ?: emptyList() else emptyList()

                val orderResponse = apiService.getOrderById(orderId)

                if (orderResponse.isSuccessful && orderResponse.body() != null) {
                    val loadedOrder = orderResponse.body()!!

                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            availableClients = clientsList,
                            originalOrder = loadedOrder,

                            selectedClientId = loadedOrder.client?.id,
                            detail = loadedOrder.detail ?: "",
                            timeExtension = loadedOrder.timeExtension?.toString() ?: "",
                            deliveryDate = loadedOrder.deliveryDate ?: "",
                            total = loadedOrder.total.toString(),
                            isPaid = loadedOrder.isPaid,
                            isCompleted = loadedOrder.isCompleted
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar orden.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun toggleEditMode() { _uiState.update { it.copy(isEditing = !it.isEditing) } }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteOrder() {
        val id = _uiState.value.orderId
        if (id == 0) return

        _uiState.update { it.copy(showDeleteDialog = false, isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteOrder(id)

                if (response.isSuccessful) {

                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun onClientSelected(id: Int) { _uiState.update { it.copy(selectedClientId = id) } }
    fun onDetailChange(text: String) { _uiState.update { it.copy(detail = text) } }
    fun onTimeExtensionChange(text: String) { if (text.all { it.isDigit() }) _uiState.update { it.copy(timeExtension = text) } }
    fun onTotalChange(text: String) { if (text.matches(Regex("^\\d*\\.?\\d*\$"))) _uiState.update { it.copy(total = text) } }
    fun onPaidChange(paid: Boolean) { _uiState.update { it.copy(isPaid = paid) } }

    fun onCompletedChange(completed: Boolean) { _uiState.update { it.copy(isCompleted = completed) } }

    fun onDateSelected(millis: Long?) {
        if (millis != null) {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            _uiState.update { it.copy(deliveryDate = formatter.format(Date(millis))) }
        }
    }

    fun updateOrder() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = UpdateOrderRequest(
                    clientId = state.selectedClientId,
                    detail = if (state.detail.isBlank()) null else state.detail,
                    timeExtension = if (state.timeExtension.isBlank()) null else state.timeExtension.toInt(),
                    deliveryDate = if (state.deliveryDate.isBlank()) null else state.deliveryDate,
                    total = if (state.total.isBlank()) null else state.total.toDouble(),
                    isPaid = state.isPaid,
                    isCompleted = state.isCompleted
                )

                val response = apiService.updateOrder(state.orderId, request)

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
}
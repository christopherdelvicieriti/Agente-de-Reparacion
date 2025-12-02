package com.delvicier.fixagent.ui.screens.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.model.ClientRequest
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientEditUiState(
    val id: Int = 0,
    val name: String = "",
    val address: String = "",
    val idCard: String = "",
    val phone1: String = "",
    val phone2: String = "",
    val email: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,

    val showDeleteDialog: Boolean = false
)

class ClientsEditViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientEditUiState())
    val uiState = _uiState.asStateFlow()

    fun loadClient(clientId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getClientById(clientId)

                if (response.isSuccessful && response.body() != null) {
                    val client = response.body()!!
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            id = client.id,
                            name = client.name,
                            // USAR ELVIS OPERATOR (?: "") PARA EVITAR CRASHES
                            address = client.address ?: "",
                            idCard = client.idCard ?: "", // <-- AQUÍ ESTABA EL ERROR
                            phone1 = client.phone1,
                            phone2 = client.phone2 ?: "",
                            email = client.email ?: ""
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo cargar el cliente.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun onNameChange(text: String) { _uiState.update { it.copy(name = text) } }
    fun onAddressChange(text: String) { _uiState.update { it.copy(address = text) } }
    fun onIdCardChange(text: String) { if (text.all { it.isDigit() }) _uiState.update { it.copy(idCard = text) } }
    fun onPhone1Change(text: String) { _uiState.update { it.copy(phone1 = text) } }
    fun onPhone2Change(text: String) { _uiState.update { it.copy(phone2 = text) } }
    fun onEmailChange(text: String) { _uiState.update { it.copy(email = text) } }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDeleteCancel() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteClient() {
        val id = _uiState.value.id
        if (id == 0) return

        _uiState.update { it.copy(showDeleteDialog = false, isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteClient(id)

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
    fun updateClient() {
        val state = _uiState.value

        if (state.name.isBlank() || state.phone1.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nombre y Teléfono Principal son obligatorios.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ClientRequest(
                    name = state.name,
                    address = state.address,
                    idCard = if (state.idCard.isBlank()) null else state.idCard,
                    phone1 = state.phone1,
                    phone2 = if (state.phone2.isBlank()) null else state.phone2,
                    email = if (state.email.isBlank()) null else state.email
                )

                val response = apiService.updateClient(state.id, request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }
}
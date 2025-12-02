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

data class ClientAddUiState(
    val name: String = "",
    val address: String = "",
    val idCard: String = "",
    val phone1: String = "",
    val phone2: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class ClientsAddViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientAddUiState())
    val uiState = _uiState.asStateFlow()


    fun onNameChange(text: String) { _uiState.update { it.copy(name = text) } }
    fun onAddressChange(text: String) { _uiState.update { it.copy(address = text) } }
    fun onIdCardChange(text: String) {

        if (text.all { it.isDigit() }) _uiState.update { it.copy(idCard = text) }
    }
    fun onPhone1Change(text: String) { _uiState.update { it.copy(phone1 = text) } }
    fun onPhone2Change(text: String) { _uiState.update { it.copy(phone2 = text) } }
    fun onEmailChange(text: String) { _uiState.update { it.copy(email = text) } }


    fun createClient() {
        val state = _uiState.value


        if (state.name.isBlank() || state.phone1.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Nombre y Teléfono son obligatorios.") }
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


                val response = apiService.createClient(request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al crear cliente (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
}
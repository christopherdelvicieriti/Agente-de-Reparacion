package com.delvicier.fixagent.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.model.ChangePasswordRequest
import com.delvicier.fixagent.data.model.ChangeUsernameRequest
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val username: String = "Cargando...",

    val isPasswordForm: Boolean = true,

    val currentPass: String = "",
    val newPass: String = "",
    val confirmPass: String = "",

    val newUsernameInput: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ProfileViewModel(
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!.username
                    _uiState.update { it.copy(username = user, newUsernameInput = user) } // Pre-llenamos el input también
                }
            } catch (e: Exception) { }
        }
    }

    fun toggleForm() {
        _uiState.update {
            it.copy(
                isPasswordForm = !it.isPasswordForm,
                errorMessage = null,
                successMessage = null
            )
        }
    }
    fun onCurrentPassChange(text: String) { _uiState.update { it.copy(currentPass = text) } }
    fun onNewPassChange(text: String) { _uiState.update { it.copy(newPass = text) } }
    fun onConfirmPassChange(text: String) { _uiState.update { it.copy(confirmPass = text) } }

    fun onNewUsernameInputChange(text: String) { _uiState.update { it.copy(newUsernameInput = text) } }

    fun updateUsername() {
        val state = _uiState.value
        if (state.newUsernameInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre de usuario no puede estar vacío") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ChangeUsernameRequest(state.newUsernameInput)
                val response = apiService.updateUsername(request)

                if (response.isSuccessful && response.body() != null) {
                    val updatedName = response.body()!!.username
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "Nombre de usuario actualizado",
                            username = updatedName
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }
    fun changePassword() {
        val state = _uiState.value
        if (state.currentPass.isBlank() || state.newPass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Complete todos los campos") }
            return
        }
        if (state.newPass != state.confirmPass) {
            _uiState.update { it.copy(errorMessage = "Las nuevas contraseñas no coinciden") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = ChangePasswordRequest(state.currentPass, state.newPass)
                val response = apiService.changePassword(request)

                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = response.body()?.message ?: "Contraseña actualizada",
                            currentPass = "", newPass = "", confirmPass = ""
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Verifique su contraseña actual") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    fun clearMessages() { _uiState.update { it.copy(errorMessage = null, successMessage = null) } }
}
package com.delvicier.fixagent.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.model.LoginRequest
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val user: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)

class LoginViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUserChanged(user: String) { _uiState.update { it.copy(user = user) } }
    fun onPassChanged(pass: String) { _uiState.update { it.copy(pass = pass) } }

    fun onLoginClicked() {
        val user = _uiState.value.user.trim()
        val pass = _uiState.value.pass.trim()

        if (user.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingrese usuario y contraseña") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val request = LoginRequest(user, pass)

                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {

                    val token = response.body()!!.access_token

                    preferencesRepository.saveToken(token)

                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                } else {
                    val msg = if (response.code() == 401) "Credenciales incorrectas" else "Error del servidor (${response.code()})"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun onNavigationHandled() {
        _uiState.update { it.copy(isLoginSuccess = false) }
    }
}
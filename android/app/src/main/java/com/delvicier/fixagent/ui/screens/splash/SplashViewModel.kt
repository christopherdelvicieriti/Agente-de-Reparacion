package com.delvicier.fixagent.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.network.ApiService
import com.delvicier.fixagent.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _navigateTo = MutableStateFlow<Screen?>(null)
    val navigateTo = _navigateTo.asStateFlow()

    fun checkLocalConfig() {
        viewModelScope.launch {
            try {
                val baseUrl = preferencesRepository.baseUrl.first()
                val token = preferencesRepository.token.first()

                if (baseUrl.isNullOrEmpty()) {
                    _navigateTo.value = Screen.Welcome
                } else if (!token.isNullOrEmpty()) {
                    _navigateTo.value = Screen.Main
                } else {
                    val response = apiService.checkServerStatus()

                    if (response.isSuccessful && response.body() != null) {
                        val isConfigured = response.body()!!.isConfigured
                        if (isConfigured) {
                            _navigateTo.value = Screen.Login
                        } else {
                            _navigateTo.value = Screen.SetupAccount
                        }
                    } else {
                        _navigateTo.value = Screen.Welcome
                    }
                }
            } catch (e: Exception) {
                _navigateTo.value = Screen.Welcome
            }
        }
    }
}
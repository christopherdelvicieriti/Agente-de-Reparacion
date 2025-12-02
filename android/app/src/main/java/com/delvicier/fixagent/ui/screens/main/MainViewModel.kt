package com.delvicier.fixagent.ui.screens.main

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class TopBarAction(
    val icon: ImageVector,
    val description: String,
    val onClick: () -> Unit
)

class MainViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {
    val topBarActions = mutableStateMapOf<String, TopBarAction>()

    fun registerAction(route: String, action: TopBarAction) {
        topBarActions[route] = action
    }

    fun unregisterAction(route: String) {
        topBarActions.remove(route)
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            preferencesRepository.clearToken()
            viewModelScope.launch(Dispatchers.Main) {
                onLogoutComplete()
            }
        }
    }
}
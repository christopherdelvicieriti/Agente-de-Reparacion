package com.delvicier.fixagent.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.delvicier.fixagent.ui.navigation.Screen

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToWelcome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSetup: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val navigateTo by viewModel.navigateTo.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.checkLocalConfig()
    }

    LaunchedEffect(navigateTo) {
        when (navigateTo) {
            is Screen.Welcome -> onNavigateToWelcome()
            is Screen.Login -> onNavigateToLogin()
            is Screen.SetupAccount -> onNavigateToSetup()
            is Screen.Main -> onNavigateToMain()
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
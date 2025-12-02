package com.delvicier.fixagent.ui.screens.welcome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class WelcomeUiState(
    val isLoading: Boolean = false,
    val scanProgress: String? = null,
    val errorMessage: String? = null,
    val navigationEvent: Boolean = false,
    val debugMessage: String? = null
)

class WelcomeViewModel(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState = _uiState.asStateFlow()

    private var scanJob: Job? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(2, TimeUnit.SECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .build()

    private fun generateIpList(): List<String> {
        val ips = mutableListOf("10.0.2.2")
        listOf(0, 1).forEach { subnet ->
            for (i in 1..255) {
                ips.add("192.168.$subnet.$i")
            }
        }
        return ips
    }

    fun startFastScan() {
        cancelScan()
        _uiState.update { it.copy(isLoading = true, errorMessage = null, scanProgress = "Iniciando modo rápido...") }

        scanJob = viewModelScope.launch(Dispatchers.IO) {
            val allIps = generateIpList()

            val batches = allIps.chunked(30)

            for (batch in batches) {
                if (scanJob?.isActive == false) break

                _uiState.update { it.copy(scanProgress = "Escaneando rango: ${batch.first()}...") }

                val deferreds = batch.map { ip ->
                    async {
                        val url = "http://$ip:4000"
                        if (checkConnection(url)) url else null
                    }
                }

                val results = deferreds.awaitAll()

                val foundUrl = results.firstOrNull { it != null }

                if (foundUrl != null) {
                    onServerFound(foundUrl)
                    return@launch
                }
            }

            if (scanJob?.isActive == true) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se encontró servidor. Intenta el modo 1 por 1 o revisa que el servidor esté corriendo.") }
            }
        }
    }

    fun startSlowScan() {
        cancelScan()
        _uiState.update { it.copy(isLoading = true, errorMessage = null, scanProgress = "Iniciando modo seguro...") }

        scanJob = viewModelScope.launch(Dispatchers.IO) {
            val allIps = generateIpList()

            for (ip in allIps) {
                if (scanJob?.isActive == false) break

                val url = "http://$ip:4000"
                _uiState.update { it.copy(scanProgress = "Probando: $ip") }
                Log.d("FixAgentScan", "Probando: $url")

                if (checkConnection(url)) {
                    onServerFound(url)
                    return@launch
                }
            }

            if (scanJob?.isActive == true) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se encontró servidor en la red.") }
            }
        }
    }

    fun saveExternalUrl(urlInput: String) {
        val cleanUrl = urlInput.trim().removeSuffix("/")

        if (cleanUrl.isEmpty() || !cleanUrl.startsWith("http")) {
            _uiState.update { it.copy(errorMessage = "URL inválida. Debe empezar con http:// o https://") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, scanProgress = "Verificando...") }

        viewModelScope.launch(Dispatchers.IO) {
            if (checkConnection(cleanUrl)) {
                onServerFound(cleanUrl)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudo conectar a $cleanUrl") }
            }
        }
    }


    private suspend fun onServerFound(url: String) {
        Log.d("FixAgentScan", "¡ENCONTRADO!: $url")
        preferencesRepository.saveBaseUrl(url)
        _uiState.update { it.copy(isLoading = false, navigationEvent = true, scanProgress = "¡Conectado!") }
    }

    private fun checkConnection(baseUrl: String): Boolean {
        return try {

            val request = Request.Builder().url("$baseUrl/api").get().build()
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    fun cancelScan() {
        scanJob?.cancel()
        _uiState.update { it.copy(isLoading = false, scanProgress = null, errorMessage = "Escaneo cancelado.") }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
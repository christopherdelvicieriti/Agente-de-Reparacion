package com.delvicier.fixagent.ui.screens.config

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delvicier.fixagent.ui.screens.welcome.WelcomeViewModel

private enum class ConfigType { NONE, LOCAL, EXTERNAL }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigNetworkScreen(
    viewModel: WelcomeViewModel,
    onConfigComplete: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var configType by remember { mutableStateOf(ConfigType.NONE) }
    var externalUrl by remember { mutableStateOf("") }

    val warningColor = MaterialTheme.colorScheme.errorContainer
    val onWarningColor = MaterialTheme.colorScheme.onErrorContainer

    LaunchedEffect(uiState.navigationEvent) {
        if (uiState.navigationEvent) {
            onConfigComplete()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Conexión Perdida", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.WifiOff,
                contentDescription = "Sin conexión",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Parece que la dirección del servidor ha cambiado o no está disponible.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = warningColor),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = onWarningColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = uiState.scanProgress ?: "Buscando nuevo servidor...", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.cancelScan() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cancelar")
                }

            } else {
                when (configType) {
                    ConfigType.NONE -> {
                        Text("Selecciona un método para reconectar:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                            Button(
                                onClick = { configType = ConfigType.LOCAL },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Escaneo Automático (Local)")
                            }

                            OutlinedButton(
                                onClick = { configType = ConfigType.EXTERNAL },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text("Ingresar URL Manualmente")
                            }
                        }
                    }

                    ConfigType.LOCAL -> {
                        Text("Escaneo de Red Local", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Buscaremos el servidor en 192.168.x.x",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = { viewModel.startFastScan() },
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) { Text("Escaneo Rápido") }

                            Button(
                                onClick = { viewModel.startSlowScan() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) { Text("Escaneo Profundo") }

                            TextButton(
                                onClick = { configType = ConfigType.NONE; viewModel.clearError() },
                                modifier = Modifier.fillMaxWidth(),
                            ) { Text("Atrás") }
                        }
                    }

                    ConfigType.EXTERNAL -> {
                        Text("Configuración Manual", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = externalUrl,
                            onValueChange = { externalUrl = it; viewModel.clearError() },
                            label = { Text("Nueva URL") },
                            placeholder = { Text("http://192.168.1.50:4000") },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { viewModel.saveExternalUrl(externalUrl) },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) { Text("Guardar y Conectar") }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { configType = ConfigType.NONE; viewModel.clearError() }
                        ) { Text("Atrás") }
                    }
                }
            }
        }
    }
}
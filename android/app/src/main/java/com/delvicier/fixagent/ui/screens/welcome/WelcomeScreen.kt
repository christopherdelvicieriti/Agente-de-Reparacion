package com.delvicier.fixagent.ui.screens.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.delvicier.fixagent.R
import com.delvicier.fixagent.ui.theme.MyColors.BrandBlue
import com.delvicier.fixagent.ui.theme.MyColors.BrandPurple

private enum class ServerType { NONE, LOCAL, EXTERNAL }

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var serverType by remember { mutableStateOf(ServerType.NONE) }
    var externalUrl by remember { mutableStateOf("") }

    val errorColor = MaterialTheme.colorScheme.error

    LaunchedEffect(uiState.navigationEvent) {
        if (uiState.navigationEvent) {
            onNavigateToLogin()
        }
    }

    val gradientColors = listOf(BrandPurple, BrandBlue)
    val buttonBrush = Brush.horizontalGradient(gradientColors)

    Scaffold(
        modifier = Modifier.systemBarsPadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.calvo1),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(290.dp)
                    //.shadow(10.dp, RoundedCornerShape(16.dp), spotColor = Color.Black.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Bienvenid@", style = MaterialTheme.typography.displaySmall)
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (uiState.isLoading) {

                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = uiState.scanProgress ?: "Procesando...", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.cancelScan() },
                    colors = ButtonDefaults.buttonColors(containerColor = errorColor)
                ) {
                    Text("Cancelar proceso")
                }
            } else {

                when (serverType) {
                    ServerType.NONE -> {
                        Text("¿Desde dónde te quieres conectar?", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(24.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            GradientButton("Servidor Local", buttonBrush) { serverType = ServerType.LOCAL }
                            GradientButton("Servidor Externo", buttonBrush) { serverType = ServerType.EXTERNAL }
                        }
                    }

                    ServerType.LOCAL -> {
                        Text("Escaneo Automático de IPs", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Selecciona el método de búsqueda:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Button(
                                onClick = { viewModel.startFastScan() },
                                modifier = Modifier.fillMaxWidth().height(50.dp)
                            ) {
                                Text("Escaneo Rápido")
                            }


                            Button(
                                onClick = { viewModel.startSlowScan() },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("Escanear Profundo")
                            }

                            OutlinedButton(
                                onClick = {
                                    serverType = ServerType.NONE; viewModel.clearError()
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                )
                            ) {
                                Text("Volver")
                            }
                        }
                    }

                    ServerType.EXTERNAL -> {

                        Text("Conexión externa", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = externalUrl,
                            onValueChange = { externalUrl = it; viewModel.clearError() },
                            label = { Text("URL Server") },
                            placeholder = { Text("https://mi-servidor.com:4000") },
                            singleLine = true,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.saveExternalUrl(externalUrl) },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) { Text("Verificar") }
                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { serverType = ServerType.NONE; viewModel.clearError() }
                        ) { Text("Volver") }
                    }
                }
            }
        }
    }
}

@Composable
fun GradientButton(text: String, brush: Brush, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(50.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp)),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White)
        }
    }
}
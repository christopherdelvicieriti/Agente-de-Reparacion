package com.delvicier.fixagent.ui.screens.machines

import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.delvicier.fixagent.data.model.Machine
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.components.GradientButtonTwo

@Composable
fun MachinesScreen(
    orderId: Int,
    factory: ViewModelFactory,
    onNavigateToAddMachine: () -> Unit,
    onNavigateToEditMachine: (Int) -> Unit
) {
    val viewModel: MachinesViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    fun openImage(url: String?) {
        if (url != null) {
            selectedImageUrl = url
            showImageDialog = true
        }
    }

    if (showImageDialog && selectedImageUrl != null) {
        FullImageDialog(
            imageUrl = selectedImageUrl!!,
            onDismiss = { showImageDialog = false }
        )
    }

    LaunchedEffect(orderId) {
        viewModel.loadMachinesForOrder(orderId)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Máquinas agregadas",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 9.dp)
        )

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (uiState.machines.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No hay máquinas registradas aún.",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            } else {
                uiState.machines.forEach { machine ->
                    MachineItem(
                        machine = machine,
                        baseUrl = uiState.baseUrl,
                        onClick = { onNavigateToEditMachine(machine.id) },
                        onImageClick = { fullUrl ->
                            openImage(fullUrl)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (uiState.machines.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
            }

            GradientButtonTwo(
                text = "Agregar Equipo",
                onClick = onNavigateToAddMachine,
                shape = RoundedCornerShape(24.dp),
                height = 40.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(24.dp))
            )
        }
    }
}

@Composable
fun MachineItem(
    machine: Machine,
    baseUrl: String,
    onClick: () -> Unit,
    onImageClick: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .clickable(enabled = !machine.imgAnverso.isNullOrEmpty()) {
                        onImageClick("$baseUrl/${machine.imgAnverso}")
                    }
            ) {
                if (!machine.imgAnverso.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("$baseUrl/${machine.imgAnverso}")
                            .crossfade(true)
                            .build(),
                        contentDescription = machine.model,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.BrokenImage)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f).clickable { onClick() }) {
                Text(
                    text = machine.model,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!machine.accessories.isNullOrEmpty()) {
                    Text(
                        text = machine.accessories,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Costo: $${machine.repairCost}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            if (!machine.space?.color.isNullOrEmpty()){
                val itemColor = try {
                    Color(parseColor(machine.space.color))
                } catch (e: Exception) {
                    MaterialTheme.colorScheme.primary
                }
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Editar Espacio",
                    tint = itemColor,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
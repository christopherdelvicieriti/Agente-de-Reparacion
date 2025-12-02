package com.delvicier.fixagent.ui.screens.spaces

import android.graphics.Color.parseColor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.delvicier.fixagent.data.model.Space
import com.delvicier.fixagent.ui.navigation.Screen
import com.delvicier.fixagent.ui.screens.machines.FullImageDialog
import com.delvicier.fixagent.ui.screens.main.MainViewModel
import com.delvicier.fixagent.ui.screens.main.TopBarAction

@Composable
fun SpacesScreen(
    mainViewModel: MainViewModel,
    viewModel: SpacesViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToConfig: () -> Unit
) {
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

    DisposableEffect(Unit) {
        mainViewModel.registerAction(
            route = Screen.Spaces.route,
            action = TopBarAction(
                icon = Icons.Default.Add,
                description = "Agregar Espacio",
                onClick = onNavigateToAdd
            )
        )
        onDispose { mainViewModel.unregisterAction(Screen.Spaces.route) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSpaces()
    }

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        if (uiState.errorMessage != null) {
            Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {

                Text(
                    text = "Error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = uiState.errorMessage!!, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { viewModel.loadSpaces() }) { Text("Reintentar") }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        onNavigateToConfig()
                    }
                ) {
                    Text("Cambiar Configuración de Red")
                }
            }
        }

        if (!uiState.isLoading && uiState.spaces.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.spaces) { space ->
                    SpaceItem(
                        space = space,
                        baseUrl = uiState.baseUrl,
                        onClick = { onNavigateToEdit(space.id) },
                        onImageClick = { fullUrl ->
                            openImage(fullUrl)
                        }
                    )
                }
            }
        } else if (!uiState.isLoading && uiState.spaces.isEmpty() && uiState.errorMessage == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No tienes espacios creados aún.",
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onNavigateToAdd) { Text("Crear el primero") }
                }
            }
        }
    }
}

@Composable
fun SpaceItem(
    space: Space,
    baseUrl: String,
    onClick: () -> Unit,
    onImageClick: (String) -> Unit
) {
    val itemColor = try {
        Color(parseColor(space.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable(enabled = !space.image.isNullOrEmpty()) {
                        onImageClick("$baseUrl/${space.image}")
                    }
            ) {
                if (!space.image.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("$baseUrl/${space.image}")
                            .crossfade(true)
                            .build(),
                        contentDescription = space.alias,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        error = rememberVectorPainter(Icons.Default.ImageNotSupported)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ImageNotSupported,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f).clickable { onClick() },) {
                Text(
                    text = space.alias,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = space.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    maxLines = 2
                )
            }


            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Editar Espacio",
                    tint = itemColor,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Editar Espacio",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun rememberVectorPainter(image: androidx.compose.ui.graphics.vector.ImageVector) =
    androidx.compose.ui.graphics.vector.rememberVectorPainter(image)
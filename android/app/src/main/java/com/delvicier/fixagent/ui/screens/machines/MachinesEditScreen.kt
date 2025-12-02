package com.delvicier.fixagent.ui.screens.machines

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ImageNotSupported
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import bottomBorder
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.components.GradientButtonTwo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachinesEditScreen(
    machineId: Int,
    factory: ViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: MachinesEditViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val inputShape = RoundedCornerShape(24.dp)

    var showImageDialog by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    fun openImage(url: String?) {
        if (url != null) {
            selectedImageUrl = url
            showImageDialog = true
        }
    }

    var isSpaceExpanded by remember { mutableStateOf(false) }
    val selectedSpaceName = uiState.selectedSpace?.alias ?: "Sin espacio asignado"

    LaunchedEffect(Unit) { viewModel.loadData(machineId) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Operación exitosa", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    if (showImageDialog && selectedImageUrl != null) {
        FullImageDialog(
            imageUrl = selectedImageUrl!!,
            onDismiss = { showImageDialog = false }
        )
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDeleteCancel() },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.primary,
            title = { Text("Eliminar máquina") },
            text = { Text("¿Confirma eliminar este equipo del sistema?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteMachine() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onDeleteCancel() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            val topBarShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            CenterAlignedTopAppBar(
                title = { Text("Editar Máquina $machineId", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
                },
                actions = {
                    IconButton(onClick = { viewModel.onDeleteClick() }) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .shadow(elevation = 2.dp, shape = topBarShape)
                    .clip(topBarShape)
                    .background(MaterialTheme.colorScheme.background)
                    .bottomBorder(
                        strokeWidth = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (uiState.errorMessage != null) {

                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.loadData(machineId) }) { Text("Reintentar") }
                }
            }

            if (uiState.originalMachine != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Evidencia Fotográfica",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = uiState.formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                        ReadOnlyPhoto("Anverso", uiState.imgAnversoUrl) { openImage(uiState.imgAnversoUrl) }
                        ReadOnlyPhoto("Reverso", uiState.imgReversoUrl) { openImage(uiState.imgReversoUrl) }
                        ReadOnlyPhoto("Accesorios", uiState.imgAccessoriesUrl) { openImage(uiState.imgAccessoriesUrl) }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


                    OutlinedTextField(
                        value = uiState.model,
                        onValueChange = viewModel::onModelChange,
                        label = { Text("Marca y modelo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = inputShape,
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text("Descripción / Falla") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = inputShape,
                        maxLines = 3
                    )

                    OutlinedTextField(
                        value = uiState.accessories,
                        onValueChange = viewModel::onAccessoriesChange,
                        label = { Text("Accesorios") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = inputShape
                    )

                    OutlinedTextField(
                        value = uiState.cost,
                        onValueChange = viewModel::onCostChange,
                        label = { Text("Costo final ($)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = inputShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    Column {
                        ExposedDropdownMenuBox(
                            expanded = isSpaceExpanded,
                            onExpandedChange = { isSpaceExpanded = !isSpaceExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedSpaceName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Ubicación / Espacio") },
                                leadingIcon = if (uiState.selectedSpace != null) {
                                    {
                                        val color = try {
                                            Color(android.graphics.Color.parseColor(uiState.selectedSpace!!.color))
                                        } catch (e: Exception) { Color.Gray }
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                    }
                                } else null,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSpaceExpanded) },
                                shape = inputShape,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )

                            ExposedDropdownMenu(
                                expanded = isSpaceExpanded,
                                onDismissRequest = { isSpaceExpanded = false }
                            ) {
                                uiState.availableSpaces.forEach { space ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                val color = try { Color(android.graphics.Color.parseColor(space.color)) } catch (e: Exception) { Color.Gray }
                                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(space.alias)
                                            }
                                        },
                                        onClick = {
                                            viewModel.onSpaceSelected(space.id)
                                            isSpaceExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        if (uiState.selectedSpace?.image != null) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxSize()) {

                                    val spaceImageUrl = "${uiState.baseUrl}/${uiState.selectedSpace!!.image}"

                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(spaceImageUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Foto espacio",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        error = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.ImageNotSupported)
                                    )

                                    Surface(
                                        color = Color.Black.copy(alpha = 0.6f),
                                        modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth()
                                    ) {
                                        Text(
                                            text = uiState.selectedSpace!!.description,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(8.dp),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    if (uiState.selectedSpace?.image == null) {
                        Spacer(modifier = Modifier.height(0.dp))
                    }

                    GradientButtonTwo(
                        text = "Guardar Cambios",
                        onClick = { viewModel.updateMachine() },
                        shape = inputShape,
                        height = 50.dp,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(24.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun ReadOnlyPhoto(label: String, imageUrl: String?, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(enabled = imageUrl != null) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.05f)))
            } else {
                Icon(Icons.Default.ImageNotSupported, contentDescription = null, tint = Color.LightGray)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}


@Composable
fun FullImageDialog(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen completa",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
            }
        }
    }
}
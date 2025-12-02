package com.delvicier.fixagent.ui.screens.orders

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bottomBorder
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.components.GradientButtonTwo
import com.delvicier.fixagent.ui.screens.machines.MachinesScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersEditScreen(
    orderId: Int,
    factory: ViewModelFactory,
    onBack: () -> Unit,
    onNavigateToAddMachine: (Int) -> Unit,
    onNavigateToEditMachine: (Int) -> Unit
) {
    val viewModel: OrdersEditViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val inputShape = RoundedCornerShape(24.dp)

    LaunchedEffect(Unit) { viewModel.loadData(orderId) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Orden actualizada correctamente", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) { DatePicker(state = datePickerState) }
    }

    var isClientExpanded by remember { mutableStateOf(false) }
    val selectedClientName = uiState.availableClients.find { it.id == uiState.selectedClientId }?.name ?: "Cliente no asignado"

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDeleteCancel() },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = MaterialTheme.colorScheme.primary,
            title = { Text("Eliminar Orden") },
            text = {
                Text("¿Estás seguro de eliminar la Orden $orderId? Esto borrará también las máquinas asociadas.")
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteOrder() },
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
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            val topBarShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            CenterAlignedTopAppBar(
                title = { Text("Orden $orderId", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver") }
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
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = { viewModel.loadData(orderId) }) { Text("Reintentar") }
                }
            }

            if (uiState.originalOrder != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    if (!uiState.isEditing) {

                        Card(
                            // shape = RoundedCornerShape(16.dp),
                            // elevation = CardDefaults.cardElevation(2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            ),
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = uiState.originalOrder?.detail ?: "Sin detalle",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Cliente: ${uiState.originalOrder?.client?.name ?: "Eliminado"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Total: $${uiState.originalOrder?.total}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(onClick = { viewModel.toggleEditMode() }) {
                                    Icon(Icons.Default.Edit, "Editar", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(0.dp))

                        MachinesScreen(
                            orderId = orderId,
                            factory = factory,
                            onNavigateToAddMachine = { onNavigateToAddMachine(orderId) },
                            onNavigateToEditMachine = { machineId ->
                                onNavigateToEditMachine(machineId)
                            }
                        )
                    }

                    if (uiState.isEditing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp)
                        ) {
                            IconButton(onClick = { viewModel.toggleEditMode() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Cerrar edición",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            Text(
                                text = "Editar",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = { viewModel.onDeleteClick() }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar Orden",
                                    tint = Color.Red
                                )
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = isClientExpanded,
                            onExpandedChange = { isClientExpanded = !isClientExpanded },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedClientName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Reasignar Cliente") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isClientExpanded) },
                                shape = inputShape,
                                modifier = Modifier.fillMaxWidth().menuAnchor(),
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                            )
                            ExposedDropdownMenu(
                                expanded = isClientExpanded,
                                onDismissRequest = { isClientExpanded = false }
                            ) {
                                uiState.availableClients.forEach { client ->
                                    DropdownMenuItem(
                                        text = { Text(client.name) },
                                        onClick = {
                                            viewModel.onClientSelected(client.id)
                                            isClientExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = uiState.detail,
                            onValueChange = viewModel::onDetailChange,
                            label = { Text("Detalle") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            shape = inputShape,
                            maxLines = 3
                        )

                        OutlinedTextField(
                            value = uiState.timeExtension,
                            onValueChange = viewModel::onTimeExtensionChange,
                            label = { Text("Días Extras") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            shape = inputShape,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        OutlinedTextField(
                            value = uiState.deliveryDate,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Entrega") },
                            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }.padding(bottom = 16.dp),
                            enabled = false,
                            shape = inputShape,
                            trailingIcon = { Icon(Icons.Default.DateRange, null) },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        OutlinedTextField(
                            value = uiState.total,
                            onValueChange = viewModel::onTotalChange,
                            label = { Text("Total ($)") },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            shape = inputShape,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("¿Cobrado?", style = MaterialTheme.typography.bodyLarge)
                            Switch(
                                checked = uiState.isPaid,
                                onCheckedChange = viewModel::onPaidChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Estado de la Orden", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    if (uiState.isCompleted) "Completado / Entregado" else "Pendiente / En Proceso",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (uiState.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF9800)
                                )
                            }
                            Switch(
                                checked = uiState.isCompleted,
                                onCheckedChange = viewModel::onCompletedChange,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                                    uncheckedThumbColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                                )
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        GradientButtonTwo(
                            text = "Guardar",
                            onClick = { viewModel.updateOrder() },
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
}
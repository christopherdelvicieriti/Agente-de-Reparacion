package com.delvicier.fixagent.ui.screens.orders

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import bottomBorder
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.components.GradientButtonTwo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersAddScreen(
    factory: ViewModelFactory,
    onBack: () -> Unit,
    onNavigateToCreateClient: () -> Unit
) {
    val viewModel: OrdersAddViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val inputShape = RoundedCornerShape(24.dp)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshClients()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var isClientExpanded by remember { mutableStateOf(false) }
    val selectedClientName = uiState.availableClients.find { it.id == uiState.selectedClientId }?.name ?: ""
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Orden creada exitosamente", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            val topBarShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            CenterAlignedTopAppBar(
                title = { Text("Nueva Orden", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Text("Cliente *", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = isClientExpanded,
                        onExpandedChange = { isClientExpanded = !isClientExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedClientName,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Buscar cliente") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isClientExpanded) },
                            shape = inputShape,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )

                        ExposedDropdownMenu(
                            expanded = isClientExpanded,
                            onDismissRequest = { isClientExpanded = false }
                        ) {
                            if (uiState.availableClients.isEmpty()) {
                                DropdownMenuItem(text = { Text("Sin clientes. ¡Crea uno!") }, onClick = { })
                            } else {
                                uiState.availableClients.forEach { client ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(client.name, fontWeight = FontWeight.Bold)
                                                Text("Cédula: ${client.idCard ?: "S/N"}", style = MaterialTheme.typography.bodySmall)
                                            }
                                        },
                                        onClick = {
                                            viewModel.onClientSelected(client.id)
                                            isClientExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                FilledIconButton(
                    onClick = onNavigateToCreateClient,
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Crear Cliente Nuevo",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }


            OutlinedTextField(
                value = uiState.detail,
                onValueChange = viewModel::onDetailChange,
                label = { Text("Detalle del Trabajo") },
                modifier = Modifier.fillMaxWidth(),
                shape = inputShape,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                val addText = { textToAdd: String ->
                    val currentText = uiState.detail
                    val newText = if (currentText.isBlank()) textToAdd else "$currentText, $textToAdd"
                    viewModel.onDetailChange(newText)
                }

                SuggestionChip(
                    onClick = { addText("Reparar Laptop") },
                    label = {
                        Text(
                            text = "Laptop",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50)
                )

                SuggestionChip(
                    onClick = { addText("Reparar Impresora") },
                    label = {
                        Text(
                            text = "Printer",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50)
                )

                SuggestionChip(
                    onClick = { addText("Reparar CPU") },
                    label = {
                        Text(
                            text = "Desktop",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50)
                )
            }

            OutlinedTextField(
                value = uiState.deliveryDate,
                onValueChange = { },
                readOnly = true,
                label = { Text("Fecha de Entrega") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = inputShape,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }
            )

            OutlinedTextField(
                value = uiState.total,
                onValueChange = viewModel::onTotalChange,
                label = { Text("Total Estimado ($)") },
                modifier = Modifier.fillMaxWidth(),
                shape = inputShape,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.weight(1f))

            GradientButtonTwo(
                text = "Crear Orden",
                onClick = { viewModel.createOrder() },
                shape = inputShape,
                height = 50.dp,
                enabled = !uiState.isLoading && uiState.selectedClientId != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(24.dp))
            )
        }
    }
}
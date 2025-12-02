package com.delvicier.fixagent.ui.screens.orders

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delvicier.fixagent.data.model.Order
import com.delvicier.fixagent.ui.navigation.Screen
import com.delvicier.fixagent.ui.screens.main.MainViewModel
import com.delvicier.fixagent.ui.screens.main.TopBarAction
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun OrdersScreen(
    mainViewModel: MainViewModel,
    viewModel: OrdersViewModel = viewModel(),
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToConfig: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSortDescending by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        mainViewModel.registerAction(
            route = Screen.Orders.route,
            action = TopBarAction(
                icon = Icons.Default.Add,
                description = "Nueva Orden",
                onClick = onNavigateToAdd
            )
        )
        onDispose { mainViewModel.unregisterAction(Screen.Orders.route) }
    }

    LaunchedEffect(Unit) { viewModel.loadOrders() }

    val filteredOrders = remember(uiState.orders, searchQuery, isSortDescending) {

        var list = if (searchQuery.isNotBlank()) {
            uiState.orders.filter { order ->
                val matchName = order.client?.name?.contains(searchQuery, ignoreCase = true) == true

                val matchId = order.client?.cedula?.contains(searchQuery, ignoreCase = true) == true

                val matchDetail = order.detail?.contains(searchQuery, ignoreCase = true) == true

                matchName || matchId || matchDetail
            }
        } else {
            uiState.orders
        }

        list = list.sortedWith(
            compareBy<Order> { order ->

                if (order.isCompleted) 1 else 0
            }.thenBy { order ->

                if (isSortDescending) -order.id else order.id
            }
        )
        list
    }

    Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar por cliente...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = if (searchQuery.isNotEmpty()) {
                        {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Borrar")
                            }
                        }
                    } else null,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )

                FilledIconButton(
                    onClick = { isSortDescending = !isSortDescending },
                    modifier = Modifier.size(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Sort,
                        contentDescription = "Ordenar",
                        modifier = Modifier.scale(scaleY = if (isSortDescending) 1f else -1f, scaleX = 1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(text = uiState.errorMessage!!, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = { viewModel.loadOrders() }) { Text("Reintentar") }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            onNavigateToConfig()
                        }
                    ) {
                        Text("Cambiar Configuración de Red")
                    }

                }
            } else {
                if (filteredOrders.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredOrders) { order ->
                            OrderItem(order, onClick = { onNavigateToEdit(order.id) })
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (uiState.orders.isEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No hay órdenes registradas.", color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = onNavigateToAdd) { Text("Crear la primera") }
                            }
                        } else {
                            Text("No se encontraron resultados.", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: Order, onClick: () -> Unit) {

    val statusText = if (order.isCompleted) "Entregado" else "Pendiente"
    val statusColor = if (order.isCompleted) Color(0xFF4CAF50) else Color(0xFFFF9800)
    val statusBg = statusColor.copy(alpha = 0.1f)

    val paymentText = if (order.isPaid) "Cobrado" else "$${order.total}"
    val paymentColor = if (order.isPaid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
    val paymentBg = paymentColor.copy(alpha = 0.1f)
    val paymentWeight = if (order.isPaid) FontWeight.Bold else FontWeight.Normal

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(12.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = formatDate(order.client?.fechaCreacion),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Text(
                    text = order.detail ?: "Sin descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .background(statusBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(paymentBg, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = paymentText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = paymentWeight,
                            color = paymentColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = order.client?.name ?: "Cliente desconocido",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver detalle",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun formatDate(isoDate: String?): String {
    if (isoDate.isNullOrEmpty()) return "Sin fecha"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(isoDate)

        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        if (date != null) outputFormat.format(date) else isoDate
    } catch (e: Exception) {
        isoDate
    }
}
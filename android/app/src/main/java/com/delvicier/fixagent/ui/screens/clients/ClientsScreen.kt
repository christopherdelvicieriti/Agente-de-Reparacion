package com.delvicier.fixagent.ui.screens.clients

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.delvicier.fixagent.data.model.Client
import com.delvicier.fixagent.ui.navigation.Screen
import com.delvicier.fixagent.ui.screens.main.MainViewModel
import com.delvicier.fixagent.ui.screens.main.TopBarAction
import com.delvicier.fixagent.ui.theme.MyColors.BrandBlue
import com.delvicier.fixagent.ui.theme.MyColors.BrandPurple

@Composable
fun ClientsScreen(
    mainViewModel: MainViewModel,
    viewModel: ClientsViewModel = viewModel(),
    onNavigateToAdd: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onNavigateToConfig: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var isSortDescending by remember { mutableStateOf(true) }

    fun makePhoneCall(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se puede realizar la llamada", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        mainViewModel.registerAction(
            route = Screen.Clients.route,
            action = TopBarAction(
                icon = Icons.Default.PersonAdd,
                description = "Nuevo Cliente",
                onClick = onNavigateToAdd
            )
        )
        onDispose { mainViewModel.unregisterAction(Screen.Clients.route) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadClients()
    }
    val filteredClients = remember(uiState.clients, searchQuery, isSortDescending) {
        var list = uiState.clients

        if (searchQuery.isNotBlank()) {
            list = list.filter { client ->
                var matchName = client.name.contains(searchQuery, ignoreCase = true)
                var matchIdCard = client.idCard?.contains(searchQuery, ignoreCase = true) == true
                var matchAddress = client.address?.contains(searchQuery, ignoreCase = true)  == true

                matchName || matchIdCard || matchAddress
            }
        }

        if (isSortDescending) {
            list.sortedByDescending { it.id }
        } else {
            list.sortedBy { it.id }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
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
                    placeholder = { Text("Buscar cliente...") },
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

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "Error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = uiState.errorMessage!!, style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = { viewModel.loadClients() }) { Text("Reintentar") }

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
            } else {

                if (filteredClients.isNotEmpty()) {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredClients) { client ->
                            ClientItem(
                                client = client,
                                onEditClick = { onNavigateToEdit(client.id) },
                                onCardClick = {
                                    val phone = client.phone1.ifEmpty { client.phone2 }
                                    if (!phone.isNullOrEmpty()) {
                                        makePhoneCall(phone)
                                    } else {
                                        Toast.makeText(context, "Sin número registrado", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                } else {

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (uiState.clients.isEmpty()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("No hay clientes registrados.", color = Color.Gray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = onNavigateToAdd) { Text("Crear el primero") }
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
fun ClientItem(
    client: Client,
    onEditClick: () -> Unit,
    onCardClick: () -> Unit
) {
    val gradientColors = listOf(BrandPurple, BrandBlue)
    val btnbrush = Brush.horizontalGradient(gradientColors)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(btnbrush, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = if (!client.name.isNullOrEmpty()) {
                        client.name.take(1).uppercase()
                    } else "?"

                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            headlineContent = {
                Text(
                    text = client.name ?: "Sin Nombre",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (!client.phone1.isNullOrEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = client.phone1,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            },
            trailingContent = {
                FilledIconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar Cliente",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}
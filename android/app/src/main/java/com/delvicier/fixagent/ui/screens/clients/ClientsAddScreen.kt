package com.delvicier.fixagent.ui.screens.clients

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bottomBorder
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.components.GradientButtonTwo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsAddScreen(
    factory: ViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: ClientsAddViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val inputShape = RoundedCornerShape(24.dp)

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Cliente registrado exitosamente", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            val topBarShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Cliente", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text("Datos Personales", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nombre Completo *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = inputShape,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = uiState.phone1,
                onValueChange = viewModel::onPhone1Change,
                label = { Text("Teléfono Principal *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = inputShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = uiState.phone2,
                onValueChange = viewModel::onPhone2Change,
                label = { Text("Convencional (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = inputShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = uiState.address,
                onValueChange = viewModel::onAddressChange,
                label = { Text("Dirección (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                shape = inputShape,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = uiState.idCard,
                onValueChange = viewModel::onIdCardChange,
                label = { Text("Cédula (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = inputShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = inputShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.weight(1f))

            GradientButtonTwo(
                text = "Guardar Cliente",
                onClick = { viewModel.createClient() },
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
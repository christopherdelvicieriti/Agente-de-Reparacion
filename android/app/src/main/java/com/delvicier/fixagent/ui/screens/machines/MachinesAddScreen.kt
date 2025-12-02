package com.delvicier.fixagent.ui.screens.machines

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ImageNotSupported
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bottomBorder
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.delvicier.fixagent.ui.ViewModelFactory
import com.delvicier.fixagent.ui.components.GradientButtonTwo
import com.delvicier.fixagent.ui.screens.spaces.createTempPictureUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MachinesAddScreen(
    orderId: Int,
    factory: ViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: MachinesAddViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val inputShape = RoundedCornerShape(24.dp)

    var tempImageUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoType by remember { mutableStateOf<MachineImageType?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempImageUri != null && currentPhotoType != null) {
            viewModel.uploadMachineImage(tempImageUri!!, context, currentPhotoType!!)
        }
    }

    fun launchCamera(type: MachineImageType) {
        currentPhotoType = type
        val uri = createTempPictureUri(context)
        tempImageUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Máquina agregada", Toast.LENGTH_SHORT).show()
            onBack()
        }
    }


    var isSpaceExpanded by remember { mutableStateOf(false) }
    val selectedSpaceName = uiState.selectedSpace?.alias ?: "Sin espacio asignado"

    Scaffold(
        topBar = {
            val topBarShape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            CenterAlignedTopAppBar(
                title = { Text("Nueva Máquina", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }

            Text("Fotografías", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PhotoSelector(
                    label = "Anverso",
                    imageUrl = uiState.imgAnversoUrl,
                    onClick = { launchCamera(MachineImageType.ANVERSO) }
                )
                PhotoSelector(
                    label = "Reverso",
                    imageUrl = uiState.imgReversoUrl,
                    onClick = { launchCamera(MachineImageType.REVERSO) }
                )
                PhotoSelector(
                    label = "Accesorios",
                    imageUrl = uiState.imgAccessoriesUrl,
                    onClick = { launchCamera(MachineImageType.ACCESORIOS) }
                )
            }


            OutlinedTextField(
                value = uiState.model,
                onValueChange = viewModel::onModelChange,
                label = { Text("Marca y modelo *") },
                modifier = Modifier.fillMaxWidth(),
                shape = inputShape,
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Falla / Descripción") },
                modifier = Modifier.fillMaxWidth(),
                shape = inputShape,
                maxLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            OutlinedTextField(
                value = uiState.accessories,
                onValueChange = viewModel::onAccessoriesChange,
                label = { Text("Accesorios recibidos") },
                modifier = Modifier.fillMaxWidth(),
                shape = inputShape,
                maxLines = 2,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            OutlinedTextField(
                value = uiState.cost,
                onValueChange = viewModel::onCostChange,
                label = { Text("Costo inicial ($)") },
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
                                    Color(android.graphics.Color.parseColor(uiState.selectedSpace?.color))
                                } catch (e: Exception) { Color.Gray }
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(1.dp, Color.LightGray, CircleShape)
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
                        modifier = Modifier.fillMaxWidth().height(150.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val spaceImageUrl = "${uiState.baseUrl}/${uiState.selectedSpace?.image}"
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
                                uiState.selectedSpace?.description?.let {
                                    Text(
                                        text = it,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (uiState.selectedSpace?.image == null) {
                Spacer(modifier = Modifier.weight(1f))
            }

            GradientButtonTwo(
                text = "Guardar Máquina",
                onClick = { viewModel.createMachine(orderId) },
                shape = inputShape,
                height = 50.dp,
                enabled = !uiState.isLoading && uiState.model.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(24.dp))
            )
        }
    }
}

@Composable
fun PhotoSelector(label: String, imageUrl: String?, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}
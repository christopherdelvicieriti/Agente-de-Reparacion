package com.delvicier.fixagent.ui.screens.machines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.delvicier.fixagent.data.local.PreferencesRepository
import com.delvicier.fixagent.data.model.Machine
import com.delvicier.fixagent.data.model.Space
import com.delvicier.fixagent.data.model.UpdateMachineRequest
import com.delvicier.fixagent.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class MachineEditUiState(
    val machineId: Int = 0,
    val originalMachine: Machine? = null,
    val originalOrderId: Int = 0,

    val formattedDate: String = "",
    val baseUrl: String = "",

    val model: String = "",
    val description: String = "",
    val accessories: String = "",
    val cost: String = "",

    val selectedSpaceId: Int? = null,
    val selectedSpace: Space? = null,

    val imgAnversoUrl: String? = null,
    val imgReversoUrl: String? = null,
    val imgAccessoriesUrl: String? = null,

    val imgAnversoPath: String? = null,
    val imgReversoPath: String? = null,
    val imgAccessoriesPath: String? = null,

    val availableSpaces: List<Space> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val showDeleteDialog: Boolean = false
)

class MachinesEditViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(MachineEditUiState())
    val uiState = _uiState.asStateFlow()

    fun loadData(machineId: Int) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, machineId = machineId) }

        viewModelScope.launch(Dispatchers.IO) {
            try {

                val baseUrl = preferencesRepository.baseUrl.first()?.removeSuffix("/") ?: ""

                val spacesResponse = apiService.getSpaces()
                val spaces = if (spacesResponse.isSuccessful) spacesResponse.body() ?: emptyList() else emptyList()

                val machineResponse = apiService.getMachineById(machineId)

                if (machineResponse.isSuccessful && machineResponse.body() != null) {
                    val machine = machineResponse.body()!!

                    val prettyDate = formatDate(machine.entryDate)

                    val currentSpace = spaces.find { it.id == machine.space?.id }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            baseUrl = baseUrl,

                            availableSpaces = spaces,
                            originalMachine = machine,
                            originalOrderId = machine.order?.id ?: 0,
                            formattedDate = prettyDate,

                            model = machine.model,
                            description = machine.description ?: "",
                            accessories = machine.accessories ?: "",
                            cost = machine.repairCost.toString(),

                            selectedSpaceId = machine.space?.id,
                            selectedSpace = currentSpace,

                            imgAnversoUrl = if (machine.imgAnverso != null) "$baseUrl/${machine.imgAnverso}" else null,
                            imgReversoUrl = if (machine.imgReverso != null) "$baseUrl/${machine.imgReverso}" else null,
                            imgAccessoriesUrl = if (machine.imgAccessories != null) "$baseUrl/${machine.imgAccessories}" else null,

                            imgAnversoPath = machine.imgAnverso,
                            imgReversoPath = machine.imgReverso,
                            imgAccessoriesPath = machine.imgAccessories
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar máquina (${machineResponse.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    private fun formatDate(isoDate: String?): String {
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

    fun onModelChange(text: String) { _uiState.update { it.copy(model = text) } }
    fun onDescriptionChange(text: String) { _uiState.update { it.copy(description = text) } }
    fun onAccessoriesChange(text: String) { _uiState.update { it.copy(accessories = text) } }

    fun onCostChange(text: String) {
        if (text.matches(Regex("^\\d*\\.?\\d*\$"))) _uiState.update { it.copy(cost = text) }
    }

    fun onSpaceSelected(id: Int) {
        val space = _uiState.value.availableSpaces.find { it.id == id }
        _uiState.update { it.copy(selectedSpaceId = id, selectedSpace = space) }
    }

    fun updateMachine() {
        val state = _uiState.value
        if (state.model.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El modelo es obligatorio") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = UpdateMachineRequest(
                    orderId = state.originalOrderId,
                    model = state.model,
                    spaceId = state.selectedSpaceId,
                    repairCost = state.cost.toDoubleOrNull() ?: 0.0,
                    description = if (state.description.isBlank()) null else state.description,
                    accessories = if (state.accessories.isBlank()) null else state.accessories,

                    imgAnverso = state.imgAnversoPath,
                    imgReverso = state.imgReversoPath,
                    imgAccessories = state.imgAccessoriesPath
                )

                val response = apiService.updateMachine(state.machineId, request)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al actualizar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error de conexión: ${e.message}") }
            }
        }
    }

    fun onDeleteClick() { _uiState.update { it.copy(showDeleteDialog = true) } }
    fun onDeleteCancel() { _uiState.update { it.copy(showDeleteDialog = false) } }

    fun deleteMachine() {
        val id = _uiState.value.machineId
        if (id == 0) return

        _uiState.update { it.copy(showDeleteDialog = false, isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.deleteMachine(id)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al eliminar (${response.code()})") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }
}
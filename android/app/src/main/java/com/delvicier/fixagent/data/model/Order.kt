package com.delvicier.fixagent.data.model

import androidx.compose.ui.graphics.Color

import com.google.gson.annotations.SerializedName

data class Order(
    val id: Int,

    @SerializedName("fechaCreacion")
    val createdAt: String,

    @SerializedName("extension_tiempo")
    val timeExtension: Int?,

    @SerializedName("detalle")
    val detail: String?,

    @SerializedName("entrega")
    val deliveryDate: String?,

    @SerializedName("estado")
    val isCompleted: Boolean,

    @SerializedName("cobrado")
    val isPaid: Boolean,

    val total: Double,
    val client: OrderClient?
)

data class OrderClient(
    val id: Int,
    @SerializedName("nombre")
    val name: String,
    val cedula: String?,
    @SerializedName("dirección")
    val address: String?,
    val telf1: String?,
    val telf2: String?,
    val email: String?,
    val fechaCreacion: String,
)

data class CreateOrderRequest(
    @SerializedName("id_client")
    val clientId: Int,

    @SerializedName("detalle")
    val detail: String?,

    @SerializedName("extension_tiempo")
    val timeExtension: Int?,

    @SerializedName("entrega")
    val deliveryDate: String?,

    @SerializedName("total")
    val total: Double?,

    @SerializedName("cobrado")
    val isPaid: Boolean = false,

    @SerializedName("estado")
    val isCompleted: Boolean = false
)

data class UpdateOrderRequest(
    @SerializedName("id_client") val clientId: Int?,
    @SerializedName("detalle") val detail: String?,
    @SerializedName("extension_tiempo") val timeExtension: Int?,
    @SerializedName("entrega") val deliveryDate: String?,
    @SerializedName("total") val total: Double?,
    @SerializedName("cobrado") val isPaid: Boolean?,
    @SerializedName("estado") val isCompleted: Boolean?
)

enum class OrderStatus(val label: String, val color: Color) {
    PENDING("Pendiente", Color(0xFFFFA000)),
    IN_PROGRESS("En Progreso", Color(0xFF1976D2)),
    COMPLETED("Completada", Color(0xFF388E3C)),
    CANCELLED("Cancelada", Color(0xFFD32F2F))
}
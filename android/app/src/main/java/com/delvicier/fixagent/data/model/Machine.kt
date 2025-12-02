package com.delvicier.fixagent.data.model

import com.google.gson.annotations.SerializedName

data class Machine(
    val id: Int,

    @SerializedName("modelo")
    val model: String,
    @SerializedName("descripcion")
    val description: String?,
    @SerializedName("accesorios")
    val accessories: String?,
    @SerializedName("costo_arreglo")
    val repairCost: Double,
    @SerializedName("fechaIngreso")
    val entryDate: String,
    @SerializedName("img_anverso")
    val imgAnverso: String?,
    @SerializedName("img_reverso")
    val imgReverso: String?,
    @SerializedName("img_accesorios")
    val imgAccessories: String?,
    @SerializedName("order")
    val order: Order?,
    @SerializedName("space")
    val space: Space?
)

data class CreateMachineRequest(
    @SerializedName("id_order") val orderId: Int,
    @SerializedName("modelo") val model: String,
    @SerializedName("id_spaces") val spaceId: Int?,
    @SerializedName("costo_arreglo") val repairCost: Double?,
    @SerializedName("descripcion") val description: String?,
    @SerializedName("accesorios") val accessories: String?,

    @SerializedName("img_anverso") val imgAnverso: String?,
    @SerializedName("img_reverso") val imgReverso: String?,
    @SerializedName("img_accesorios") val imgAccessories: String?
)

data class UpdateMachineRequest(
    @SerializedName("id_order") val orderId: Int?,
    @SerializedName("modelo") val model: String?,
    @SerializedName("id_spaces") val spaceId: Int?,
    @SerializedName("costo_arreglo") val repairCost: Double?,
    @SerializedName("descripcion") val description: String?,
    @SerializedName("accesorios") val accessories: String?,

    @SerializedName("img_anverso") val imgAnverso: String?,
    @SerializedName("img_reverso") val imgReverso: String?,
    @SerializedName("img_accesorios") val imgAccessories: String?
)
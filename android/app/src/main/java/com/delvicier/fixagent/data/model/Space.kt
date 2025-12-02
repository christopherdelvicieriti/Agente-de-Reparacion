package com.delvicier.fixagent.data.model

import com.google.gson.annotations.SerializedName

data class Space(
    val id: Int,
    val alias: String,
    @SerializedName("descripcion")
    val description: String,
    val image: String?,
    val color: String,
    @SerializedName("fechaCreacion")
    val createdAt: String
)
data class SpaceRequest(
    val alias: String,
    val descripcion: String,
    val image: String,
    val color: String
)

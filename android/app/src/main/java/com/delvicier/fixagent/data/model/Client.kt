package com.delvicier.fixagent.data.model

import com.google.gson.annotations.SerializedName

data class Client(
    val id: Int,
    @SerializedName("nombre")
    val name: String,
    @SerializedName("dirección")
    val address: String?,
    @SerializedName("cedula")
    val idCard: String?,
    @SerializedName("telf1")
    val phone1: String,
    @SerializedName("telf2")
    val phone2: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("fechaCreacion")
    val createdAt: String
)

data class ClientRequest(
    @SerializedName("nombre")
    val name: String,
    @SerializedName("dirección")
    val address: String,
    @SerializedName("cedula")
    val idCard: String?,
    @SerializedName("telf1")
    val phone1: String,
    @SerializedName("telf2")
    val phone2: String?,
    @SerializedName("email")
    val email: String?
)
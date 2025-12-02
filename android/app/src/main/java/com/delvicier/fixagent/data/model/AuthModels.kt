package com.delvicier.fixagent.data.model

import com.google.gson.annotations.SerializedName

data class RecoverRequest(
    @SerializedName("secret_key") val secretKey: String
)

data class RecoverResponse(
    @SerializedName("reset_token") val resetToken: String
)

data class ResetPasswordRequest(
    @SerializedName("newPassword") val newPassword: String
)
data class LoginRequest(
    val usuario: String,
    val contraseña: String
)

data class LoginResponse(
    val access_token: String
)

data class SetupRequest(
    val usuario: String,
    val contraseña: String
)

data class SetupResponse(
    val message: String,
    val secret_key: String
)

data class StatusResponse(
    val isConfigured: Boolean
)
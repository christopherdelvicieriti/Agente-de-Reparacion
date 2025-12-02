package com.delvicier.fixagent.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    val id: Int,
    @SerializedName("usuario") val username: String,
    @SerializedName("fechaCreacion") val createdAt: String
)

data class ChangeUsernameRequest(
    @SerializedName("usuario") val newUsername: String
)
data class ChangePasswordRequest(
    @SerializedName("oldPassword") val currentPass: String,
    @SerializedName("newPassword") val newPass: String
)

data class MessageResponse(
    val message: String
)
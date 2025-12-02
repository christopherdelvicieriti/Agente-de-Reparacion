package com.delvicier.fixagent.data.network

import com.delvicier.fixagent.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("auth/status")
    suspend fun checkServerStatus(): Response<StatusResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/setup")
    suspend fun setupAccount(
        @Header("Authorization") token: String,
        @Body request: SetupRequest
    ): Response<SetupResponse>

    @GET("spaces")
    suspend fun getSpaces(): Response<List<Space>>

    @Multipart
    @POST("images/image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @POST("spaces")
    suspend fun createSpace(@Body request: SpaceRequest): Response<Space>

    @GET("spaces/{id}")
    suspend fun getSpaceById(@Path("id") id: Int): Response<Space>

    @PATCH("spaces/{id}")
    suspend fun updateSpace(
        @Path("id") id: Int,
        @Body request: SpaceRequest
    ): Response<Space>

    @DELETE("spaces/{id}")
    suspend fun deleteSpace(@Path("id") id: Int): Response<Unit>

    @GET("clients")
    suspend fun getClients(): Response<List<Client>>

    @POST("clients")
    suspend fun createClient(@Body request: ClientRequest): Response<Client>

    @GET("clients/{id}")
    suspend fun getClientById(@Path("id") id: Int): Response<Client>

    @PATCH("clients/{id}")
    suspend fun updateClient(
        @Path("id") id: Int,
        @Body request: ClientRequest
    ): Response<Client>

    @DELETE("clients/{id}")
    suspend fun deleteClient(@Path("id") id: Int): Response<Unit>

    @GET("orders")
    suspend fun getOrders(): Response<List<Order>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<Order>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: Int): Response<Order>

    @PATCH("orders/{id}")
    suspend fun updateOrder(
        @Path("id") id: Int,
        @Body request: UpdateOrderRequest
    ): Response<Order>

    @GET("machines")
    suspend fun getMachines(): Response<List<Machine>>

    @GET("machines/by-order/{id}")
    suspend fun getMachinesByOrder(@Path("id") orderId: Int): Response<List<Machine>>

    @POST("machines")
    suspend fun createMachine(@Body request: CreateMachineRequest): Response<Machine>

    @GET("machines/{id}")
    suspend fun getMachineById(@Path("id") id: Int): Response<Machine>

    @PATCH("machines/{id}")
    suspend fun updateMachine(
        @Path("id") id: Int,
        @Body request: UpdateMachineRequest
    ): Response<Machine>

    @DELETE("machines/{id}")
    suspend fun deleteMachine(@Path("id") id: Int): Response<Unit>

    @DELETE("orders/{id}")
    suspend fun deleteOrder(@Path("id") id: Int): Response<Unit>

    @GET("user/profile")
    suspend fun getUserProfile(): Response<UserProfileResponse>

    @PUT("user/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<MessageResponse>

    @PUT("user/profile")
    suspend fun updateUsername(@Body request: ChangeUsernameRequest): Response<UserProfileResponse>

    @POST("auth/recover")
    suspend fun recoverPassword(@Body request: RecoverRequest): Response<RecoverResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Header("Authorization") resetToken: String,
        @Body request: ResetPasswordRequest
    ): Response<MessageResponse>
}
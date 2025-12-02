package com.delvicier.fixagent.data.network

import com.delvicier.fixagent.data.local.PreferencesRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val DUMMY_URL = "http://localhost/"

    fun getClient(preferencesRepository: PreferencesRepository): ApiService {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val hostSelectionInterceptor = HostSelectionInterceptor(preferencesRepository)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(hostSelectionInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(DUMMY_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
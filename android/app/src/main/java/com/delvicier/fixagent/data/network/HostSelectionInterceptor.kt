package com.delvicier.fixagent.data.network

import com.delvicier.fixagent.data.local.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.io.IOException

class HostSelectionInterceptor(
    private val preferencesRepository: PreferencesRepository
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val baseUrlString = runBlocking {
            preferencesRepository.baseUrl.first()
        }
        val token = runBlocking {
            preferencesRepository.token.first()
        }

        if (!baseUrlString.isNullOrEmpty()) {
            val newBaseUrl = baseUrlString.toHttpUrlOrNull()

            if (newBaseUrl != null) {
                val originalUrl = request.url

                val newUrlBuilder = newBaseUrl.newBuilder()

                for (segment in originalUrl.pathSegments) {
                    newUrlBuilder.addPathSegment(segment)
                }

                newUrlBuilder.encodedQuery(originalUrl.encodedQuery)

                val finalUrl = newUrlBuilder.build()

                request = request.newBuilder()
                    .url(finalUrl)
                    .build()
            }
        }

        if (request.header("Authorization") == null && !token.isNullOrEmpty()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(request)
    }
}
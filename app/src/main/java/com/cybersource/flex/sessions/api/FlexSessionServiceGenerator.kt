package com.cybersource.flex.sessions.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

enum class Environment {
    SANDBOX,
    PRODUCTION
}

class FlexSessionServiceGenerator {

    fun getRetrofirApiService(environment: Environment): FlexSessionService {
        val gsonObject = GsonBuilder().setLenient().create()

        var baseURL: String = ""
        baseURL = when(environment) {
            Environment.SANDBOX -> "https://testflex.cybersource.com"
            Environment.PRODUCTION -> "https://flex.cybersource.com"
        }

        val retrofit = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gsonObject))
                .client(createUnsafeOkHttpClient())
                .build()
        return retrofit.create(FlexSessionService::class.java)
    }

    private fun createUnsafeOkHttpClient(): OkHttpClient {
        return try {
            val okHttpClient: OkHttpClient = OkHttpClient.Builder().apply {
            }.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.NONE
            }).build()
            return okHttpClient
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}


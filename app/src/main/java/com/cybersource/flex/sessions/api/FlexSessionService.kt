package com.cybersource.flex.sessions.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST

interface FlexSessionService {
    @Headers("Content-Type: application/json;charset=utf-8")
    @POST("/flex/v2/sessions")
    suspend fun createCaptureContext(@Body requestBody: RequestBody, @HeaderMap headers: Map<String, String>): Response<String>
}
package com.example.springtech.io

import com.example.springtech.io.response.LoginRequest
import com.example.springtech.io.response.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
   @POST("authenticate")
   suspend fun login(@Body request: LoginRequest) : LoginResponse
}
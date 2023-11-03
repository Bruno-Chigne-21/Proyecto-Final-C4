package com.example.springtech.io

import com.example.springtech.io.response.LoginResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST(value = "login")
    fun postlogin(@Query(value = "email") email: String, @Query(value = "password") password: String):
            Call<LoginResponse>

    companion object Factory{
        private const val BASE_URL = "http://192.168.84.1:8000/api/v1/auth/authenticate"
        fun create(): ApiService{
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}
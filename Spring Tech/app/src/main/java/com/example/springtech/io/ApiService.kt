package com.example.springtech.io

import com.example.springtech.io.response.LoginRequest
import com.example.springtech.io.response.LoginResponse
import com.example.springtech.io.response.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

   //Login
   @POST("authenticate")
   suspend fun login(@Body request: LoginRequest) : LoginResponse

   //Coordenadas
   @GET("technicals")
   fun obtenerDatos(
      @Header("Authorization") token: String,
      @Query("professionId") professionId: Int,
      @Query("availabilityId") availabilityId: Int,
      @Query("latitude") latitude: String,
      @Query("longitude") longitude: String,
      @Query("distance") distance: Int = 1
   ): Call<ResponseBody>

}
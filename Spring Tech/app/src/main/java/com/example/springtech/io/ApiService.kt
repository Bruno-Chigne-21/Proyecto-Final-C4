package com.example.springtech.io

import com.example.springtech.io.response.CategoryResponse
import com.example.springtech.io.response.ClientResponse
import com.example.springtech.io.response.CoordinatesResponse
import com.example.springtech.io.response.DirectResponse
import com.example.springtech.io.response.LoginRequest
import com.example.springtech.io.response.LoginResponse
import com.example.springtech.io.response.RegisterRequest
import com.example.springtech.io.response.RegisterResponse
import com.example.springtech.io.response.UpdateResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

   //Logear
   @POST("authenticate")
   suspend fun login(@Body request: LoginRequest) : LoginResponse

   //Coordenadas
   @GET("technicals")
   fun obtenerCoords(
      @Header("Authorization") token: String,
      @Query("professionId") professionId: Int,
      @Query("latitude") latitude: Double,
      @Query("longitude") longitude: Double,
      @Query("distance") distance: Int = 7
   ): Call<CoordinatesResponse>

   //Coordenadas de los que trabajan a domicilio y los que trabajan en taller
   @GET("technicals")
   fun obtenerCoordsDomTal(
      @Header("Authorization") token: String,
      @Query("professionId") professionId: Int,
      @Query("availabilityId") availabilityId: Int,
      @Query("latitude") latitude: Double,
      @Query("longitude") longitude: Double,
      @Query("distance") distance: Int = 7
   ): Call<CoordinatesResponse>

   //Actualizar
   @PUT("client/{id}")
   suspend fun updateClient(
      @Path("id") clientId: Int,
      @Header("Authorization") token: String,
      @Body client: Client
   ): Response<UpdateResponse>

   //Cliente
   @GET("user/{id}")
   suspend fun getClient(
      @Path("id") clientId: Int,
      @Header("Authorization") token: String,
   ): Response<ClientResponse>

   //Registrar cliente
   @POST("register")
   suspend fun regClient(
      @Body request : RegisterRequest
   ): Response<RegisterResponse>

   //Solicitar
   @POST("directrequest")
   @Multipart
   suspend fun solicitar(
      @Part("professionAvailabilityId") professionAvailabilityId: RequestBody,
      @Part("clientId") clientId: RequestBody,
      @Part("categoryServiceId") categoryServiceId: RequestBody,
      @Part("latitude") latitude: RequestBody,
      @Part("longitude") longitude: RequestBody,
      @Part("title") title: RequestBody,
      @Part("description") description: RequestBody,
      @Header("Authorization") token: String
   ): Response<DirectResponse>

   //Obtener categorias
   @GET("categorieService")
   suspend fun category(
      @Header("Authorization") token: String
   ): Response<CategoryResponse>
}
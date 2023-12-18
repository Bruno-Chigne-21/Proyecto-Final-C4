package com.example.springtech.io.response

data class UpdateResponse(
    val message: String,
    val body: Cliente
)

data class Cliente(
    val id: Int,
    val userId: Int,
    val name: String,
    val lastname: String,
    val motherLastname: String,
    val dni: String,
    val birthDate: Long
)
package com.example.springtech.io.response

data class ClientResponse(
    val message: String?,
    val body: UserData
)

data class UserData(
    val id: Int,
    val userId: String?,
    val name: String,
    val lastname: String,
    val motherLastname: String,
    val dni: String,
    val birthDate: Long
)

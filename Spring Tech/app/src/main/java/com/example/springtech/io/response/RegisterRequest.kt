package com.example.springtech.io.response

data class RegisterRequest(
    val email: String,
    val password: String,
    val roleId: Int,
    val name: String,
    val lastname: String,
    val motherLastname: String,
    val dni: String,
    val birthDate: String
)


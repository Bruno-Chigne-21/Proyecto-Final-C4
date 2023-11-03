package com.example.springtech.io.response

import com.example.springtech.model.User

data class LoginResponse(
    val message: String,
    val user: User,
    val token: String
)

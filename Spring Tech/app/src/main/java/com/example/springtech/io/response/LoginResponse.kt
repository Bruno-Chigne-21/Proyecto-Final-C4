package com.example.springtech.io.response

data class LoginResponse(
    val message: String,
    val body: Body
) {
    data class Body(
        val token: String
    )
}

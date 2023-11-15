package com.example.springtech.io.response

import com.example.springtech.model.User

data class LoginResponse(
    val message: String,
    val body: Body
) {
    data class Body(
        val token: String
    )
}

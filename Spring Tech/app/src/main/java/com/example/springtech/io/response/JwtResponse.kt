package com.example.springtech.io.response

data class JwtResponse(
    val sub: String,
    val roleId: Int,
    val iat: Long,
    val exp: Long
)

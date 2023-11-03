package com.example.springtech.model

data class User(
    val id: Int,
    val role_id: Int,
    val email: String,
    val password: String,
    val state: Int
)

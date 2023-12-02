package com.example.springtech.io.response

data class ResponseBody(
    val message: String,
    val body: List<User>
)

data class User(
    val id: Int,
    val name: String,
    val lastname: String,
    val motherLastname: String,
    val dni: String,
    val latitude: Double,
    val longitude: Double,
    val birthDate: Long,
    val user: UserInfo
)

data class UserInfo(
    val id: Int,
    val email: String,
    val role: UserRole,
    val state: String
)

data class UserRole(
    val id: Int,
    val name: String
)
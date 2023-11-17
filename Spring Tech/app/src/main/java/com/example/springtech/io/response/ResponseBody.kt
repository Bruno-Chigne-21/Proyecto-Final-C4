package com.example.springtech.io.response

data class ResponseBody(
    val message: String,
    val body: List<Technical>
)

data class Technical(
    val id: Int,
    val name: String,
    val lastname: String,
    val motherLastname: String,
    val dni: String,
    val latitude: String,
    val longitude: String,
    val birthDate: Long,
    val user: User,
    val professions: List<Profession>,
    val availability: Availability
)

data class User(
    val id: Int,
    val email: String,
    val role: Role,
    val state: String,
    val enabled: Boolean,
    val authorities: List<Authority>,
    val username: String,
    val credentialsNonExpired: Boolean,
    val accountNonExpired: Boolean,
    val accountNonLocked: Boolean
)

data class Role(
    val id: Int,
    val name: String
)

data class Authority(
    val authority: String
)

data class Profession(
    val id: Int,
    val name: String,
    val experience: Experience
)

data class Experience(
    val id: Int,
    val name: String
)

data class Availability(
    val id: Int,
    val name: String,
    val state: String
)


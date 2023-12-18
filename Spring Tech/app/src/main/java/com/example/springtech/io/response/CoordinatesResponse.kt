package com.example.springtech.io.response

data class Role(
    val id: Int,
    val name: String
)

data class User(
    val id: Int,
    val email: String,
    val role: Role,
    val state: String
)

data class ProfessionAvailability(
    val id: Int,
    val profession: Profession,
    val availability: Availability,
    val experience: Experience,
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0
)


data class Profession(
    val id: Int,
    val name: String
)

data class Availability(
    val id: Int,
    val name: String
)

data class Experience(
    val id: Int,
    val name: String
)

data class Person(
    val id: Int,
    val name: String,
    val lastname: String,
    val motherLastname: String,
    val dni: String,
    val latitude: Double,
    val longitude: Double,
    val birthDate: Long,
    val user: User,
    val professionAvailability: ProfessionAvailability,
    val statusWorking: String
)

data class CoordinatesResponse(
    val message: String,
    val body: List<Person>
)

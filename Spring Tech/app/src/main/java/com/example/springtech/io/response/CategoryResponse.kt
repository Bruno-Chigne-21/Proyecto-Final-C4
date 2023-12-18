package com.example.springtech.io.response

data class CategoryResponse(
    val message: String,
    val body: List<Service>
)

data class Service(
    val id: Int,
    val name: String,
    val state: String
)
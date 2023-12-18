package com.example.springtech.io.response

data class DirectResponse(
    val message: String?,
    val body: DirectResponseBody
)

data class DirectResponseBody(
    val id: Int,
    val clientId: Int,
    val categoryService: CategoryService?,
    val stateInvoice: String,
    val latitude: Double,
    val longitude: Double,
    val title: String,
    val description: String,
    val state: State?
)

data class CategoryService(
    val id: Int,
    val name: String,
    val state: String
)

data class State(
    val id: Int,
    val name: String?
)
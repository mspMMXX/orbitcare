package com.yama.orbitcare.data.models

data class Employee (
    val organisationID: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val passwordHash: String = ""
)
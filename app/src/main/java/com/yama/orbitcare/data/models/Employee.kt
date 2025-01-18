package com.yama.orbitcare.data.models

import java.util.UUID

data class Employee (
    val id: String = UUID.randomUUID().toString(),
    val organisationID: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val passwordHash: String = ""
)
package com.yama.orbitcare.data.models

import java.util.UUID

/**
 * Represents an employee in the system.
 * Each employee is associated with an organisation and contains basic contact and authentication details.
 */
data class Employee (
    val id: String = UUID.randomUUID().toString(),
    val organisationID: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phone: String = "",
    val email: String = "",
    val passwordHash: String = ""
)
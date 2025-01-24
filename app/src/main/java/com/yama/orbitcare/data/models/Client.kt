package com.yama.orbitcare.data.models

import java.util.UUID

/**
 * Represents a client in the system.
 * Each client is associated with an organisation and contains basic contact details.
 */
data class Client (
    val id: String = UUID.randomUUID().toString(),
    val orgId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val street: String = "",
    val housenumber: String = "",
    val plz: String = "",
    val city: String = "",
    val phone: String = "",
    val email: String = "",
)
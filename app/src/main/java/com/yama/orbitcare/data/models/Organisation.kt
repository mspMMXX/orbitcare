package com.yama.orbitcare.data.models

/**
 * Represents an organisation in the system.
 * Contains basic details about the organisation's name and address.
 */
data class Organisation (
    val organisationID: String = "",
    val name: String = "",
    val street: String = "",
    val housenumber: String = "",
    val plz: String = "",
    val city: String = ""
)
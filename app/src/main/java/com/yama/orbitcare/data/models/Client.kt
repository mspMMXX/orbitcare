package com.yama.orbitcare.data.models

data class Client (
    val firstName: String = "",
    val lastName: String = "",
    val street: String = "",
    val housenumber: String = "",
    val plz: String = "",
    val city: String = "",
    val phone: String = "",
    val email: String = ""
)
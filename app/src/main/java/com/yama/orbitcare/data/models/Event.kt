package com.yama.orbitcare.data.models

import java.time.LocalDateTime

data class Event (
    val id: String = "",
    val title: String = "",
    val dateTimeString: String? = null,
    val eventType: String = "",
    val notes: String = "",
    val color: String = "",
    val view: String = ""
) {
    val dateTime: LocalDateTime
        get() = LocalDateTime.parse(dateTimeString)
}
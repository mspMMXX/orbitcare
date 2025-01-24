package com.yama.orbitcare.data.models

import java.time.LocalDateTime
import java.util.UUID

/**
 * Represents an event in the system.
 * Each event is associated with an employee and contains details about the event's type, time, and appearance.
 */
data class Event (
    val id: String = UUID.randomUUID().toString(),
    val employeeId: String = "",
    val title: String = "",
    val dateTimeString: String? = null,
    val eventType: String = "",
    val notes: String = "",
    val color: String = ""
) {
    /**
     * Parsed LocalDateTime object derived from the `dateTimeString` field.
     * Throws an exception if `dateTimeString` is null or improperly formatted.
     */
    val dateTime: LocalDateTime
        get() = LocalDateTime.parse(dateTimeString)
}
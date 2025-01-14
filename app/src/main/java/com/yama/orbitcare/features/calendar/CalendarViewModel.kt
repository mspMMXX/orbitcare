package com.yama.orbitcare.features.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yama.orbitcare.data.models.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CalendarViewModel {
    // Live data for events
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    // Live data for current date
    private val _currentDate = MutableLiveData<LocalDateTime>()
    val currentDate: LiveData<LocalDateTime> = _currentDate

    // Live data for current view
    private val _currentView = MutableLiveData<CalendarActivity.CalendarView>()
    val currentView: LiveData<CalendarActivity.CalendarView> = _currentView

    // Live data for selected day
    private val _selectedDay = MutableLiveData<LocalDate>()
    val selectedDay: LiveData<LocalDate> = _selectedDay

    init {
        _currentDate.value = LocalDateTime.now()
        _currentView.value = CalendarActivity.CalendarView.MONTH
        _events.value = emptyList()
        _selectedDay.value = LocalDate.now()
    }

    // Add Event
    fun addEvent(title: String, date: LocalDate, time: LocalTime, eventType: String, notes: String = "", color: String = "", view: String = "") {
        val newEvent = Event(
            title = title,
            dateTime = LocalDateTime.of(date, time),
            eventType = eventType,
            notes = notes,
            color = color,

        )

        val currentEvents = _events.value.orEmpty().toMutableList()
        currentEvents.add(newEvent)
        _events.value = currentEvents
    }
}
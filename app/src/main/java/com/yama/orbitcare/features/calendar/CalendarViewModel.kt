package com.yama.orbitcare.features.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yama.orbitcare.data.models.Event
import java.time.LocalDate
import java.time.LocalDateTime

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
}
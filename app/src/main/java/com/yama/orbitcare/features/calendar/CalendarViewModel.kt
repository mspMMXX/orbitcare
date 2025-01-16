package com.yama.orbitcare.features.calendar

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Event
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID


@SuppressLint("NewApi")
class CalendarViewModel : ViewModel() {
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
        //loadAllEvents()
        _selectedDay.value = LocalDate.now()
    }

    // Add Event
    fun addEvent(title: String, date: LocalDate, time: LocalTime, eventType: String, notes: String = "", color: String = "") {
        val newEvent = Event(
            id = UUID.randomUUID().toString(),
            title = title,
            dateTime = LocalDateTime.of(date, time),
            eventType = eventType,
            notes = notes,
            color = color,

        )

        // Save Event to Firestore DB
        val db = FirestoreDatabase()
        db.addEvent(newEvent, onSuccess = {
            Log.d("Debugg", "Save Event success.")
        }, onFailure = {
            Log.d("Debugg", "Save Event failure.")
        })

        val currentEvents = _events.value.orEmpty().toMutableList()
        currentEvents.add(newEvent)
        _events.value = currentEvents
    }

    @SuppressLint("NewApi")
    fun saveEvent(title: String, dateStr: String, timeStr: String) {
        try {
            // Parse date and time strings to LocalDate and LocalTime
            val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val timeFormatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")

            val localDate = LocalDate.parse(dateStr, dateFormatter)
            val localTime = LocalTime.parse(timeStr, timeFormatter)

            addEvent(
                title = title,
                date = localDate,
                time = localTime,
                eventType = "Default"
            )
        } catch (e: Exception) {
            // Handle parsing errors if needed
            e.printStackTrace()
        }
    }

    fun updateEvent(oldEvent: Event, title: String, date: LocalDate, time: LocalTime, eventType: String = "", notes: String = "", color: String = "", view: String = "") {
        val currentEvents = _events.value.orEmpty().toMutableList()
        val eventIndex = currentEvents.indexOf(oldEvent)

        if (eventIndex != -1) {
            Log.d("Debugg", "EventID: ${oldEvent.id}")
            val updatedEvent = Event(
                id = oldEvent.id,
                title = title,
                dateTime = LocalDateTime.of(date, time),
                eventType = eventType,
                notes = notes,
                color = color,
                view = view
            )

            // Update - Firestore should but doesn't work?!
            val db = FirestoreDatabase()
            db.updateEvent(oldEvent.id, updatedEvent, onSuccess = {
                Log.d("Debugg", "Eventupdate success")
            }, onFailure = {
                Log.d("Debugg", "Eventupdate failure.")
            })
            currentEvents[eventIndex] = updatedEvent
            _events.value = currentEvents
        }
    }

    fun removeEvent(event: Event) {
        val currentEvents = _events.value.orEmpty().toMutableList()
        currentEvents.remove(event)
        _events.value = currentEvents
        val db = FirestoreDatabase()
        db.deleteEvent(event.id, onSuccess = {
            Log.d("Debugg", "Event deleted.")
        }, onFailure = {
            Log.d("Debugg", "Event not deleted.")
        })
    }

    // Get specific Events for specific dates
    fun getEventsForDate(date: LocalDate): List<Event> {
        return _events.value.orEmpty().filter {
            it.dateTime.toLocalDate() == date
        }
    }

    // Get specific Events for specific weeks
    @SuppressLint("NewApi")
    fun getEventsForWeek(weekStart: LocalDate): List<Event> {
        val weekEnd = weekStart.plusDays(6)
        return _events.value.orEmpty().filter { event ->
            val eventDate = event.dateTime.toLocalDate()
            !eventDate.isBefore(weekStart) && !eventDate.isAfter(weekEnd)
        }
    }

    // Get specific Events for specific months
    @SuppressLint("NewApi")
    fun getEventsForMonth(year: Int, month: Int): List<Event> {
        return _events.value.orEmpty().filter { event ->
            event.dateTime.year == year && event.dateTime.monthValue == month
        }
    }

    // Navigate through views
    @SuppressLint("NewApi")
    fun navigateNext() {
        when (_currentView.value) {
            CalendarActivity.CalendarView.MONTH -> _currentDate.value = _currentDate.value?.plusMonths(1)
            CalendarActivity.CalendarView.WEEK -> _currentDate.value = _currentDate.value?.plusWeeks(1)
            CalendarActivity.CalendarView.DAY -> _currentDate.value = _currentDate.value?.plusDays(1)
            else -> {}
        }
    }

    @SuppressLint("NewApi")
    fun navigatePrevious() {
        when (_currentView.value) {
            CalendarActivity.CalendarView.MONTH -> _currentDate.value = _currentDate.value?.minusMonths(1)
            CalendarActivity.CalendarView.WEEK -> _currentDate.value = _currentDate.value?.minusWeeks(1)
            CalendarActivity.CalendarView.DAY -> _currentDate.value = _currentDate.value?.minusDays(1)
            else -> {}
        }
    }

    // View management
    fun switchView(view: CalendarActivity.CalendarView) {
        _currentView.value = view
    }

    fun selectDay(date: LocalDate) {
        _selectedDay.value = date
    }

    private fun loadAllEvents() {
        val db = FirestoreDatabase()
        db.getAllEvents { events ->
            if (events != null) {
                _events.value = events
            } else {
                Log.d("Debugg", "No Events found.")
            }
        }
    }
}
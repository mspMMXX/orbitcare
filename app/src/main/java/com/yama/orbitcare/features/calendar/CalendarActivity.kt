package com.yama.orbitcare.features.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yama.orbitcare.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: ImageButton
    private lateinit var nextMonth: ImageButton
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var settingsButton: FloatingActionButton

    // Calendar variables
    private val calendar = Calendar.getInstance()
    private val currentDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.GERMAN)
    private var selectedDay: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initializeViews()
        setupCalendar()
        updateCalendarView()
        setupEventButton()
        // setupSettingsButton()
    }

    private fun initializeViews() {
        calendarGrid = findViewById(R.id.calendarGrid)
        monthYearText = findViewById(R.id.monthYearText)
        prevMonth = findViewById(R.id.prevMonth)
        nextMonth = findViewById(R.id.nextMonth)
        addEventButton = findViewById(R.id.addEventButton)
        settingsButton = findViewById(R.id.settingsButton)
    }

    private fun setupEventButton() {
        addEventButton.setOnClickListener{
            showAddEventDialog()
        }

        settingsButton.setOnClickListener{
            // showViewSelectionDialog()
        }
    }

    private fun showViewSelectionDialog() {

    }

    private fun showAddEventDialog() {
        // Alert Dialog to add event
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_event, null)

        val titleEdit = dialogView.findViewById<EditText>(R.id.eventTitleEdit)
        val dateEdit = dialogView.findViewById<EditText>(R.id.eventDateEdit)
        val timeEdit = dialogView.findViewById<EditText>(R.id.eventTimeEdit)

        // Add current date as default
        val currentDate = if (selectedDay != null) {
            Calendar.getInstance().apply {
                set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    selectedDay!!)
            }.time
        } else {
            Calendar.getInstance().time
        }

        dateEdit.setText(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(currentDate))
        timeEdit.setText(SimpleDateFormat("HH:mm", Locale.GERMAN).format(currentDate))

        builder.setView(dialogView)
            .setTitle("Neues Event")
            .setPositiveButton("Hinzufügen") { _, _ ->
                val eventTitle = titleEdit.text.toString()
                val eventDate = dateEdit.text.toString()
                val eventTime = timeEdit.text.toString()

                // Save event entries
                saveEvent(eventTitle, eventDate, eventTime) // Dummy Values
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun saveEvent(title: String, date: String, time: String) {
        // Add the logic - replace with firestore values
        Toast.makeText(this,
            "Event erstellt: $title am $date um $time",
            Toast.LENGTH_SHORT).show()

        updateCalendarView()
    }

    private fun updateCalendarView() {
        // Update Month and Year in Header
        monthYearText.text = dateFormat.format(calendar.time)

        // Empty Grid
        calendarGrid.removeAllViews()

        // Add Weekdays Headers
        addWeekDayHeaders()

        // First day of month
        val firstDayOfMonth = calendar.clone() as Calendar
        firstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1)

        // Days of Month
        val maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        // Monday as starting day
        var startDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 2
        if (startDayOfWeek < 0) startDayOfWeek = 6

        // Empty spaces for days before Month begins
        for (i in 0 until startDayOfWeek) {
            addEmptyDay()
        }

        // Add days of Month
        for (dayOfMonth in 1..maxDaysInMonth) {
            addDay(dayOfMonth)
        }
    }

    private fun addWeekDayHeaders() {
        val weekDays = arrayOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")
        for (day in weekDays) {
            val dayView = TextView(this).apply {
                text = day
                gravity = Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }
                setPadding(8, 16, 8, 16)
                setTextColor(Color.GRAY) // Wochentage in Grau
            }
            calendarGrid.addView(dayView)
        }
    }

    private fun addDay(dayOfMonth: Int) {
        val dayView = TextView(this).apply {
            text = dayOfMonth.toString()
            gravity = Gravity.CENTER

            // Variables for max height per day
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            val dayHeight = screenHeight / 8 // 6 Rows + Header + Week days

            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = dayHeight
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            setPadding(8, 16, 8, 16)
            textSize = 16f

            when {
                isCurrentDay(dayOfMonth) -> {
                    setBackgroundResource(R.drawable.current_day_background)
                    setTextColor(Color.WHITE)
                }
                dayOfMonth == selectedDay -> {
                    setBackgroundResource(R.drawable.selected_day_background)
                }
            }

            setOnClickListener {
                selectedDay = if (selectedDay == dayOfMonth) null else dayOfMonth

                updateCalendarView()

                val date = Calendar.getInstance().apply {
                    set(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        dayOfMonth)
                }
                Toast.makeText(context,
                    SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(date.time),
                    Toast.LENGTH_SHORT).show()
            }
        }
        calendarGrid.addView(dayView)
    }

    private fun addEmptyDay() {
        val emptyView = TextView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            setPadding(8, 16, 8, 16)
        }
        calendarGrid.addView(emptyView)
    }

    private fun isCurrentDay(dayOfMonth: Int): Boolean {
        return calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                dayOfMonth == currentDate.get(Calendar.DAY_OF_MONTH)
    }

    private fun setupCalendar() {
        // Click Listener for Navigation
        prevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            updateCalendarView()
        }

        nextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            updateCalendarView()
        }
    }
}
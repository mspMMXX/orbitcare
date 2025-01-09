package com.yama.orbitcare.features.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yama.orbitcare.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: ImageButton
    private lateinit var nextMonth: ImageButton

    // Calendar variables
    private val calendar = Calendar.getInstance()
    private val currentDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.GERMAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initializeViews()
        setupCalendar()
        updateCalendarView()
    }

    private fun initializeViews() {
        calendarGrid = findViewById(R.id.calendarGrid)
        monthYearText = findViewById(R.id.monthYearText)
        prevMonth = findViewById(R.id.prevMonth)
        nextMonth = findViewById(R.id.nextMonth)
    }

    private fun updateCalendarView() {
        // Update Month and Year in Header
        monthYearText.text = dateFormat.format(calendar.time)

        // Empty Grid
        calendarGrid.removeAllViews()

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

    private fun addDay(dayOfMonth: Int) {
        val dayView = TextView(this).apply {
            text = dayOfMonth.toString()
            gravity = Gravity.CENTER
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            setPadding(8, 16, 8, 16)

            // Check if day is today
            if (isCurrentDay(dayOfMonth)) {
                setBackgroundResource(R.drawable.current_day_background)
                setTextColor(Color.WHITE)
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
        // Add Day
        for (i in 1..31) {
            val dayView = TextView(this).apply {
                text = "$i"
                gravity = Gravity.CENTER
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }
                setPadding(8, 16, 8, 16)
            }
            calendarGrid.addView(dayView)
        }

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
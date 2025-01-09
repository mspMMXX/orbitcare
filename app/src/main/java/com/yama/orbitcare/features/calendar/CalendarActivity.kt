package com.yama.orbitcare.features.calendar

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
    }

    private fun initializeViews() {
        calendarGrid = findViewById(R.id.calendarGrid)
        monthYearText = findViewById(R.id.monthYearText)
        prevMonth = findViewById(R.id.prevMonth)
        nextMonth = findViewById(R.id.nextMonth)
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
            // Prev Month
        }

        nextMonth.setOnClickListener {
            // Next Month
        }
    }
}
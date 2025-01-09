package com.yama.orbitcare.features.calendar

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yama.orbitcare.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var selectedDateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initializeViews()
        setupCalendar()
    }

    private fun initializeViews() {
        calendarView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.selectedDateText)

        // Set date
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
        selectedDateText.text = getString(R.string.selected_date, dateFormat.format(Date()))
    }

    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = String.format(Locale.GERMAN, "%02d.%02d.%d", dayOfMonth, (month + 1), year)
            selectedDateText.text = getString(R.string.selected_date, date.format(Date()))
        }
    }
}
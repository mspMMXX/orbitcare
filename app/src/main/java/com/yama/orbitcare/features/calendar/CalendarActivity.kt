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

    private lateinit var calenderView: CalendarView
    private lateinit var selectedDateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
    }

    private fun initializeViews() {
        calenderView = findViewById(R.id.calendarView)
        selectedDateText = findViewById(R.id.selectedDateText)

        // Set date
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
        selectedDateText.text = "Ausgewähltes Datum: ${dateFormat.format(Date())}" // TODO: Set String
    }

    private fun setupCalendar() {
        calenderView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = String.format(Locale.GERMAN, "%02d.%02d.%d", dayOfMonth, (month + 1), year)
            selectedDateText.text = "Ausgewähltes Datum: $date" // TODO: Set String
        }
    }
}
package com.yama.orbitcare.features.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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

    // Calendar View enum
    private var currentView = CalendarView.MONTH

    // CalendarViewModel
    private val viewModel: CalendarViewModel by viewModels()

    enum class CalendarView {
        MONTH, WEEK, DAY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initializeViews()
        //setupCalendar()
        //updateCalendarView()
        setupEventButtons()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.currentDate.observe(this) {
            updateCalendarView()
        }

        viewModel.events.observe(this) {
            updateCalendarView()
        }

        viewModel.currentView.observe(this) {
            updateCalendarView()
        }

        viewModel.selectedDay.observe(this) {
            updateCalendarView()
        }
    }

    private fun initializeViews() {
        calendarGrid = findViewById(R.id.calendarGrid)
        monthYearText = findViewById(R.id.monthYearText)
        prevMonth = findViewById(R.id.prevMonth)
        nextMonth = findViewById(R.id.nextMonth)
        addEventButton = findViewById(R.id.addEventButton)
        settingsButton = findViewById(R.id.settingsButton)
    }

    private fun setupEventButtons() {
        addEventButton.setOnClickListener{
            showAddEventDialog()
        }

        settingsButton.setOnClickListener{
            showViewSelectionDialog()
        }
    }

    private fun showViewSelectionDialog() {
        // Create dialog for view selection
        val items = arrayOf("Monatsansicht", "Wochenansicht", "Tagesansicht")
        var selectedView = 0 // Default = Monatsansicht

        AlertDialog.Builder(this)
            .setTitle("Wähle Kalenderansicht")
            .setSingleChoiceItems(items, selectedView) { dialog, which ->
                selectedView = which
            }
            .setPositiveButton("Bestätigen") { dialog, _ ->
                when (selectedView) {
                    0 -> switchToMonthView()
                    1 -> switchToWeekView()
                    2 -> switchToDayView()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun switchToMonthView() {
        currentView = CalendarView.MONTH
        updateCalendarView()
    }

    private fun switchToWeekView() {
        currentView = CalendarView.WEEK
        updateCalendarView()
    }

    private fun switchToDayView() {
        currentView = CalendarView.DAY
        updateCalendarView()
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

        // Change layout in dependency of View
        when (currentView) {
            CalendarView.MONTH, CalendarView.WEEK -> {
                calendarGrid.orientation = GridLayout.HORIZONTAL
                calendarGrid.columnCount = 7  // 7 days per week
            }
            CalendarView.DAY -> {
                calendarGrid.orientation = GridLayout.VERTICAL
                calendarGrid.columnCount = 2  // Time and Events
            }
        }

        when (currentView) {
            CalendarView.MONTH -> updateMonthView()
            CalendarView.WEEK -> updateWeekView()
            CalendarView.DAY -> updateDayView()
        }
    }

    private fun updateMonthView() {
        // Add Weekday Headers
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

    private fun updateWeekView() {
        // Add Weekday Headers
        addWeekDayHeaders()

        // Set day to monday
        val weekStart = calendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Add days of week
        for (i in 0..6) {
            val currentCal = weekStart.clone() as Calendar
            currentCal.add(Calendar.DAY_OF_WEEK, i)

            val dayView = TextView(this).apply {
                text = currentCal.get(Calendar.DAY_OF_MONTH).toString()
                gravity = Gravity.CENTER

                // Add space for week view
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                val dayHeight = screenHeight / 2

                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = dayHeight
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(8, 8, 8, 8)
                }

                setPadding(8, 16, 8, 16)
                textSize = 16f

                // Highlight current day
                when {
                    isCurrentDay(currentCal) -> {
                        setBackgroundResource(R.drawable.current_day_background)
                        setTextColor(Color.WHITE)
                    }

                    currentCal.get(Calendar.DAY_OF_MONTH) == selectedDay -> {
                        setBackgroundResource(R.drawable.selected_day_background)
                    }
                }

                // Click Listener for days
                setOnClickListener {
                    selectedDay = currentCal.get(Calendar.DAY_OF_MONTH)
                    updateCalendarView()

                    Toast.makeText(
                        context,
                        SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(currentCal.time),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            calendarGrid.addView(dayView)
        }
    }

    private fun updateDayView() {
        calendarGrid.removeAllViews()

        // Change GridLayout to vertical
        calendarGrid.orientation = GridLayout.VERTICAL
        calendarGrid.columnCount = 2 // one column for time, one for events

        // Date of current day
        val dateHeader = TextView(this).apply {
            val dateFormat = SimpleDateFormat("EEEE, dd. MMMM", Locale.GERMAN)
            text = dateFormat.format(calendar.time)
            gravity = Gravity.CENTER
            layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                rowSpec = GridLayout.spec(0)
                columnSpec = GridLayout.spec(0, 2, GridLayout.FILL, 1f)
                setMargins(8, 8, 8, 16)
            }
            setPadding(8, 16, 8, 16)
            textSize = 18f
            setTextColor(Color.BLACK)
        }
        calendarGrid.addView(dateHeader)

        // ScrollView for hours
        val scrollView = ScrollView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.MATCH_PARENT
                this.setGravity(Gravity.FILL_VERTICAL)
                rowSpec = GridLayout.spec(1, 1f)
                columnSpec = GridLayout.spec(0, 2)
            }
        }

        // Maincontainer
        val mainContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Container for Time and Events
        val timeContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Hour View from 6 to 22
        for (hour in 6..22) {
            // Hours
            val hourRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Hourview
            val hourView = TextView(this).apply {
                text = String.format("%02d:00", hour)
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
                layoutParams = LinearLayout.LayoutParams(
                    120,
                    120
                ).apply {
                    marginEnd = 16
                }
                setTextColor(Color.GRAY)
            }
            hourRow.addView(hourView)

            // Slots for Events
            val timeSlot = TextView(this).apply {
                gravity = Gravity.TOP or Gravity.START
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    120
                )

                setPadding(8, 16, 8, 16)
                background =
                    ResourcesCompat.getDrawable(resources, R.drawable.timeslot_background, null)

                // Check if there are already Events
                setOnClickListener {
                    val timeString = String.format("%02d:00", hour)
                    showAddEventDialog() // opens dialog for new events
                }
            }
            hourRow.addView(timeSlot)

            timeContainer.addView(hourRow)
        }
        mainContainer.addView(timeContainer)
        scrollView.addView(mainContainer)
        calendarGrid.addView(scrollView)
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

    private fun isCurrentDay(checkCalendar: Calendar): Boolean {
        return checkCalendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                checkCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                checkCalendar.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)
    }

    private fun setupCalendar() {
        // Click Listener for Navigation
        prevMonth.setOnClickListener {
            when (currentView) {
                CalendarView.MONTH -> calendar.add(Calendar.MONTH, -1)
                CalendarView.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, -1)
                CalendarView.DAY -> calendar.add(Calendar.DAY_OF_YEAR, -1)
            }
            updateCalendarView()
        }

        nextMonth.setOnClickListener {
            when (currentView) {
                CalendarView.MONTH -> calendar.add(Calendar.MONTH, 1)
                CalendarView.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                CalendarView.DAY -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            updateCalendarView()
        }
    }
}
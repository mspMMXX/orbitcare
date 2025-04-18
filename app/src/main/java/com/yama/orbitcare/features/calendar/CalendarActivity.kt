package com.yama.orbitcare.features.calendar

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yama.orbitcare.R
import com.yama.orbitcare.data.models.Event
import com.yama.orbitcare.features.client.ClientActivity
import com.yama.orbitcare.features.employee.EmployeeActivity
import com.yama.orbitcare.features.globalUser.GlobalUser
import com.yama.orbitcare.features.login.SignInActivity
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarGrid: GridLayout
    private lateinit var monthYearText: TextView
    private lateinit var prevMonth: ImageButton
    private lateinit var nextMonth: ImageButton
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var settingsButton: FloatingActionButton
    // Bottom Navigation
    private lateinit var bottomNavigation: BottomNavigationView

    // Calendar variables
    private val calendar = Calendar.getInstance()
    private val currentDate = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.GERMAN)
    private var selectedDay: Int? = null

    // CalendarViewModel
    private val viewModel: CalendarViewModel by viewModels()

    // Defines available calendar views
    enum class CalendarView {
        MONTH, WEEK, DAY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)

        // Verify user authentication
        val employeeId = GlobalUser.currentUser?.id
        if (employeeId.isNullOrEmpty()) {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
            finish()
            return
        }

        // Synchronize calendar with ViewModel
        viewModel.currentDate.value?.let { initialDate ->
            calendar.set(initialDate.year,
                initialDate.monthValue -1,
                initialDate. dayOfMonth)
        }

        viewModel.initialize(employeeId)
        initializeViews()
        setupCalendar()
        setupEventButtons()
        setupObservers()
        setupBottomNavigation()
    }

    // Set up observers for LiveData from ViewModel to update UI accordingly
    private fun setupObservers() {
        // Observe data changes
        viewModel.currentDate.observe(this) { newDateTime ->
            // Synchronize calendar variable with ViewModel
            calendar.set(newDateTime.year,
                newDateTime.monthValue -1,
                newDateTime.dayOfMonth)
            updateCalendarView()
        }

        // Observe events update
        viewModel.events.observe(this) { events ->
            updateCalendarView()
        }

        // Observe view mode changes
        viewModel.currentView.observe(this) {
            updateCalendarView()
        }

        // Observe selected day changes
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
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Darkmode for NavArrows
        prevMonth.imageTintList = ColorStateList.valueOf(getColor(R.color.primary))
        nextMonth.imageTintList = ColorStateList.valueOf(getColor(R.color.primary))
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_person -> {
                    // Start ClientActivity
                    startActivity(Intent(this, ClientActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_group -> {
                    // Start EmployeeActivity
                    startActivity(Intent(this, EmployeeActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_calendar -> {
                    // Already in CalendarView
                    true
                }
                else -> false
            }
        }

        // Set active menu button
        bottomNavigation.selectedItemId = R.id.navigation_calendar
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
        val items = arrayOf(getString(R.string.monthview),
            getString(R.string.weekview), getString(R.string.dayview))
        var selectedView = 0 // Default = Monatsansicht

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.calendarview))
            .setSingleChoiceItems(items, selectedView) { dialog, which ->
                selectedView = which
            }
            .setPositiveButton(getString(R.string.confirm)) { dialog, _ ->
                when (selectedView) {
                    0 -> switchToMonthView()
                    1 -> switchToWeekView()
                    2 -> switchToDayView()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    private fun switchToMonthView() {
        viewModel.switchView(CalendarView.MONTH)
    }

    private fun switchToWeekView() {
        viewModel.switchView(CalendarView.WEEK)
    }

    private fun switchToDayView() {
        viewModel.switchView(CalendarView.DAY)
    }

    /**
     * Displays dialog for adding new calendar events
     * @param presetHour Optional hour to pre-fill in the dialog
     * @param presetMinute Optional minute to pre-fill in the dialog
     * @param presetDate Optional date to pre-fill in the dialog
     */
    private fun showAddEventDialog(presetHour: Int? = null, presetMinute: Int? = null, presetDate: Calendar? = null) {
        // Alert Dialog to add event
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_event, null)

        val titleEdit = dialogView.findViewById<EditText>(R.id.eventTitleEdit)
        val dateEdit = dialogView.findViewById<EditText>(R.id.eventDateEdit)
        val timeEdit = dialogView.findViewById<EditText>(R.id.eventTimeEdit)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.eventTypeSpinner)
        val notesEdit = dialogView.findViewById<EditText>(R.id.eventNotesEdit)
        val colorSpinner = dialogView.findViewById<Spinner>(R.id.eventColorSpinner)

        // Set up event type spinner
        val eventTypes = arrayOf(getString(R.string.doctorvisit),
            getString(R.string.housevisit), getString(R.string.officetime),
            getString(R.string.weekplanning), getString(
            R.string.feedback
        ))
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        // Set up color spinner
        val colors = arrayOf("#FF4444", "#33B5E5", "#99CC00", "#FFBB33", "#AA66CC")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors.map {
            when(it) {
                "#FF4444" -> getString(R.string.red)
                "#33B5E5" -> getString(R.string.blue)
                "#99CC00" -> getString(R.string.green)
                "#FFBB33" -> getString(R.string.orange)
                "#AA66CC" -> getString(R.string.purple)
                else -> "Default"
            }
        })
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorSpinner.adapter = colorAdapter

        // Use the calendar's current date instead of selectedDay or current date
        val eventDate = (presetDate ?: Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, selectedDay?: calendar.get(Calendar.DAY_OF_MONTH))
            if (presetHour != null) {
                set(Calendar.HOUR_OF_DAY, presetHour)
            }
            if (presetMinute != null) {
                set(Calendar.MINUTE, presetMinute)
            }
        }).time

        dateEdit.setText(SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).format(eventDate))
        timeEdit.setText(SimpleDateFormat("HH:mm", Locale.GERMAN).format(eventDate))

        builder.setView(dialogView)
            .setTitle(getString(R.string.new_event))
            .setPositiveButton(getString(R.string.add)) { _, _ ->
                val eventTitle = titleEdit.text.toString()
                val eventDate = dateEdit.text.toString()
                val eventTime = timeEdit.text.toString()
                val eventType = eventTypes[typeSpinner.selectedItemPosition]
                val notes = notesEdit.text.toString()
                val color = colors[colorSpinner.selectedItemPosition]

                // Save event entries
                viewModel.saveEvent(
                    title = eventTitle,
                    date = eventDate,
                    time = eventTime,
                    eventType = eventType,
                    notes = notes,
                    color = color
                )
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    @SuppressLint("NewApi")
    private fun showUpdateEventDialog(event: Event) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_event, null)

        val titleEdit = dialogView.findViewById<EditText>(R.id.eventTitleEdit)
        val dateEdit = dialogView.findViewById<EditText>(R.id.eventDateEdit)
        val timeEdit = dialogView.findViewById<EditText>(R.id.eventTimeEdit)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.eventTypeSpinner)
        val notesEdit = dialogView.findViewById<EditText>(R.id.eventNotesEdit)
        val colorSpinner = dialogView.findViewById<Spinner>(R.id.eventColorSpinner)

        // Set up event spinner
        val eventTypes = arrayOf(getString(R.string.doctorvisit),
            getString(R.string.housevisit), getString(R.string.officetime),
            getString(R.string.weekplanning), getString(
                R.string.feedback
            ))
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeAdapter

        // Set up color spinner
        val colors = arrayOf("#FF4444", "#33B5E5", "#99CC00", "#FFBB33", "#AA66CC")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors.map {
            when(it) {
                "#FF4444" -> getString(R.string.red)
                "#33B5E5" -> getString(R.string.blue)
                "#99CC00" -> getString(R.string.green)
                "#FFBB33" -> getString(R.string.orange)
                "#AA66CC" -> getString(R.string.purple)
                else -> "Default"
            }
        })
        colorSpinner.adapter = colorAdapter
        colorSpinner.setSelection(colors.indexOf(event.color).coerceAtLeast(0))
        // Get color index for ui
        val colorIndex = colors.indexOf(event.color)
        if (colorIndex >= 0) {
            colorSpinner.setSelection(colorIndex)
        }

        // Insert Event data
        titleEdit.setText(event.title)
        dateEdit.setText(event.dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
        timeEdit.setText(event.dateTime.format(DateTimeFormatter.ofPattern("HH:mm")))
        notesEdit.setText(event.notes)

        builder.setView(dialogView)
            .setTitle(getString(R.string.event_edit))
            .setPositiveButton(getString(R.string.refresh)) { _, _ ->
                val eventTitle = titleEdit.text.toString()
                val eventDate = dateEdit.text.toString()
                val eventTime = timeEdit.text.toString()
                val eventType = eventTypes[typeSpinner.selectedItemPosition]
                val notes = notesEdit.text.toString()
                val color = colors[colorSpinner.selectedItemPosition]

                updateEvent(event, eventTitle, eventDate, eventTime, eventType, notes, color)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .setNeutralButton(getString(R.string.delete)) { _, _ ->
                showDeleteEventDialog(event)
            }
            .show()
    }

    private fun showDeleteEventDialog(event: Event) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_event))
            .setMessage(getString(R.string.delete_event_confirmation, event.title))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                removeEvent(event)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    @SuppressLint("NewApi")
    private fun updateEvent(oldEvent: Event, title: String, date: String, time: String, eventType: String, notes: String, color: String) {
        // Parse date and time strings to LocalDate and LocalTime
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val localDate = LocalDate.parse(date, dateFormatter)
        val localTime = LocalTime.parse(time, timeFormatter)

        viewModel.updateEvent(
            oldEvent = oldEvent,
            title = title,
            date = localDate,
            time = localTime,
            eventType = eventType,
            notes = notes,
            color = color,
        )
    }

    private fun removeEvent(event: Event) {
        viewModel.removeEvent(event)
    }

    // Updates the calendar view based on current view mode (Month/Week/Day)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateCalendarView() {
        // Update Month and Year in Header
        monthYearText.text = dateFormat.format(calendar.time)

        // Empty Grid
        calendarGrid.removeAllViews()

        // Add currentView from ViewModel
        val currentView = viewModel.currentView.value ?: CalendarView.MONTH

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

    /**
     * Create a formatted TextView for displaying an event
     * Handle click events for event editing
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createEventView(event: Event): TextView {
        return TextView(this).apply {
            text = "${event.dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${event.title}"
            setTextColor(Color.WHITE)
            background = ResourcesCompat.getDrawable(resources, R.drawable.event_background, null)?.also { drawable ->
                try {
                    val color = event.color?.let { Color.parseColor(it) } ?: Color.parseColor("#3F51B5")
                    drawable.setTint(color)
                } catch (e: Exception) {
                    drawable.setTint(Color.parseColor("#3F51B5"))
                }
            }
            setPadding(4, 2, 4, 2)
            textSize = 12f
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END

            setOnClickListener {
                showUpdateEventDialog(event)
            }
        }
    }

    /**
     * Update the month view showing a grid of days with events
     * Handle day selection and event display
     */
    @RequiresApi(Build.VERSION_CODES.O)
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

        for (dayOfMonth in 1..maxDaysInMonth) {
            val dayContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(4, 4, 4, 4)
                }
                setPadding(4, 8, 4, 8)
            }

            // Add date
            val dateView = TextView(this).apply {
                text = dayOfMonth.toString()
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
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
                }
            }
            dayContainer.addView(dateView)

            // Add events for this day
            val eventsForDay = viewModel.events.value?.filter { event ->
                val eventDate = event.dateTime.toLocalDate()
                eventDate.year == calendar.get(Calendar.YEAR) &&
                        eventDate.monthValue == calendar.get(Calendar.MONTH) + 1 &&
                        eventDate.dayOfMonth == dayOfMonth
            }

            eventsForDay?.take(2)?.forEach { event ->
                dayContainer.addView(createEventView(event))
            }

            // Show if there are more events
            if ((eventsForDay?.size ?: 0) > 2) {
                val moreEventsView = TextView(this).apply {
                    text = "+${eventsForDay!!.size - 2} mehr"
                    setTextColor(Color.GRAY)
                    textSize = 10f
                    gravity = Gravity.CENTER
                }
                dayContainer.addView(moreEventsView)
            }

            calendarGrid.addView(dayContainer)
        }
    }

    /**
     * Update the week view showing a grid of days with events
     * Handle day selection and event display
     * Show events in scrollview
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWeekView() {
        calendarGrid.removeAllViews()

        // Add weekday headers row
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(0, 2)
            }
        }

        // Time column header (empty space)
        headerRow.addView(TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(120, LinearLayout.LayoutParams.WRAP_CONTENT)
        })

        // Set calendar to week start (Monday)
        val weekStart = calendar.clone() as Calendar
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        // Add day headers
        for (i in 0..6) {
            val currentCal = weekStart.clone() as Calendar
            currentCal.add(Calendar.DAY_OF_WEEK, i)

            val dayHeader = TextView(this).apply {
                val dayFormat = SimpleDateFormat("EE\ndd.MM.", Locale.GERMAN)
                text = dayFormat.format(currentCal.time)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding(4, 8, 4, 8)
                setTextColor(if (isCurrentDay(currentCal)) Color.WHITE else Color.GRAY)

                when {
                    isCurrentDay(currentCal) -> {
                        setBackgroundResource(R.drawable.current_day_background)
                    }
                    currentCal.get(Calendar.DAY_OF_MONTH) == selectedDay -> {
                        setBackgroundResource(R.drawable.selected_day_background)
                    }
                }

                setOnClickListener {
                    selectedDay = if (selectedDay == currentCal.get(Calendar.DAY_OF_MONTH))
                        null
                    else
                        currentCal.get(Calendar.DAY_OF_MONTH)
                    calendar.set(Calendar.DAY_OF_MONTH, currentCal.get(Calendar.DAY_OF_MONTH))
                    updateCalendarView()
                }
            }
            headerRow.addView(dayHeader)
        }
        calendarGrid.addView(headerRow)

        // Create ScrollView for time grid
        val scrollView = ScrollView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = 0
                rowSpec = GridLayout.spec(1, 1f)
                columnSpec = GridLayout.spec(0, 2)
            }
        }

        // Container for time grid
        val timeGridContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Create time slots for each hour
        for (hour in 6..22) {
            val hourRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Hour label
            val hourLabel = TextView(this).apply {
                text = String.format("%02d:00", hour)
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
                layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                    marginEnd = 16
                }
                setTextColor(Color.GRAY)
            }
            hourRow.addView(hourLabel)

            // Day columns
            for (i in 0..6) {
                val currentCal = weekStart.clone() as Calendar
                currentCal.add(Calendar.DAY_OF_WEEK, i)
                currentCal.set(Calendar.HOUR_OF_DAY, hour)

                val daySlot = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    setPadding(4, 2, 4, 2)
                    background = ResourcesCompat.getDrawable(resources, R.drawable.timeslot_background, null)

                    setOnClickListener {
                        val clickedDate = currentCal.clone() as Calendar
                        clickedDate.set(Calendar.HOUR_OF_DAY, hour)
                        clickedDate.set(Calendar.MINUTE, 0)
                        showAddEventDialog(hour, 0, clickedDate)
                    }
                }

                // Add events for this time slot
                val eventsForTimeSlot = viewModel.events.value?.filter { event ->
                    val eventDate = event.dateTime.toLocalDate()
                    val eventHour = event.dateTime.hour
                    eventDate.year == currentCal.get(Calendar.YEAR) &&
                            eventDate.monthValue == currentCal.get(Calendar.MONTH) + 1 &&
                            eventDate.dayOfMonth == currentCal.get(Calendar.DAY_OF_MONTH) &&
                            eventHour == hour
                }?.sortedBy { it.dateTime }

                eventsForTimeSlot?.forEach { event ->
                    daySlot.addView(createEventView(event))
                }

                hourRow.addView(daySlot)
            }

            timeGridContainer.addView(hourRow)
        }

        scrollView.addView(timeGridContainer)
        calendarGrid.addView(scrollView)
    }

    /**
     * Update the day view showing a scrollview with events and day times
     * Handle time selection and event display
     */
    @RequiresApi(Build.VERSION_CODES.O)
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
            setTextColor(getColor(R.color.primary))
        }
        calendarGrid.addView(dateHeader)

        // Container for Time and Events
        val timeContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Events for current day
        val eventsForDay = viewModel.events.value?.filter { event ->
            val eventDate = event.dateTime.toLocalDate()
            eventDate.year == calendar.get(Calendar.YEAR) &&
                    eventDate.monthValue == calendar.get(Calendar.MONTH) + 1 &&
                    eventDate.dayOfMonth == calendar.get(Calendar.DAY_OF_MONTH)
        }?.sortedBy { it.dateTime }

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

            // Container for Events
            val eventsContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(8, 4, 8, 4)
                background = ResourcesCompat.getDrawable(resources, R.drawable.timeslot_background, null)

                // ClickListener for new event in timeslot
                setOnClickListener {
                    // Current Date with current Time
                    val eventDateTime = calendar.clone() as Calendar
                    eventDateTime.set(Calendar.HOUR_OF_DAY, hour)
                    eventDateTime.set(Calendar.MINUTE, 0)

                    // Show event dialog
                    showAddEventDialog(hour, 0)
                }
            }

            // Show Events for current hour
            eventsForDay?.filter { event ->
                event.dateTime.hour == hour
            }?.forEach { event ->
                eventsContainer.addView(createEventView(event))
            }

            hourRow.addView(eventsContainer)

            timeContainer.addView(hourRow)
        }

        // Scrollview for event layout
        val scrollView = ScrollView(this).apply {
            layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.MATCH_PARENT
                height = 0
                rowSpec = GridLayout.spec(1, 1f)
                columnSpec = GridLayout.spec(0, 2)
            }
        }

        scrollView.addView(timeContainer)
        calendarGrid.addView(scrollView)
    }

    // Add headers for month and week view
    private fun addWeekDayHeaders() {
        val weekDays = arrayOf(getString(R.string.monday),
            getString(R.string.tuesday),
            getString(R.string.wednesday),
            getString(R.string.thursday),
            getString(R.string.friday),
            getString(R.string.saturday),
            getString(R.string.sunday))
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
                setTextColor(Color.GRAY) // Weekday in grey
            }
            calendarGrid.addView(dayView)
        }
    }

    // Calculate empty spaces for calendar grid to show days in correct spaces
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

    /**
     * Check if a given day in the current month and year is today
     * Use the class's calendar object for year and month context
     *
     * @param dayOfMonth The day to check
     * @return true if the specified day is today
     */
    private fun isCurrentDay(dayOfMonth: Int): Boolean {
        return calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                dayOfMonth == currentDate.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * Check if a given calendar date is today
     * Compare all date components with the current date
     *
     * @param checkCalendar The calendar date to check
     * @return true if the specified date is today
     */
    private fun isCurrentDay(checkCalendar: Calendar): Boolean {
        return checkCalendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                checkCalendar.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) &&
                checkCalendar.get(Calendar.DAY_OF_MONTH) == currentDate.get(Calendar.DAY_OF_MONTH)
    }

    private fun setupCalendar() {
        prevMonth.setOnClickListener {
            viewModel.navigatePrevious()
        }

        nextMonth.setOnClickListener {
            viewModel.navigateNext()
        }
    }
}
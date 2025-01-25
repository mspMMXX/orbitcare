package com.yama.orbitcare.features.employee

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Employee
import com.yama.orbitcare.features.calendar.CalendarActivity
import com.yama.orbitcare.features.client.ClientActivity
import com.yama.orbitcare.features.client.ShowClientActivity
import com.yama.orbitcare.features.globalUser.GlobalUser

/**
 * Displays a list of employees and provides navigation options.
 * Handles employee-related actions such as viewing details of an employee.
 */
class EmployeeActivity : AppCompatActivity() {

    // Instance of FirestoreDatabase for database operations
    private val db = FirestoreDatabase()

    // UI elements
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var employeeListContainer: LinearLayout

    // Local list of employees
    private var employees = mutableListOf<Employee>()

    /**
     * Called when the activity is created.
     * Sets up the UI, initializes views, and retrieves the list of employees.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_employee)

        initializeViews()
        setupBottomNavigation()
        setEmployeeList()
    }

    /**
     * Links UI components to their corresponding properties.
     */
    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        employeeListContainer = findViewById(R.id.employeeListContainer)
    }

    /**
     * Configures the bottom navigation menu with click listeners for each menu item.
     */
    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_person -> {
                    // Start ClientView
                    startActivity(Intent(this, ClientActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_group -> {
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.navigation_group
    }

    /**
     * Retrieves the list of employees from the database and displays them as buttons in the UI.
     * Filters employees based on the organisation ID of the current user.
     */
    private fun setEmployeeList() {
        val currentUser = GlobalUser.currentUser
        if (currentUser != null) {
            db.getAllEmployees { employeeList ->
                if (employeeList != null) {
                    for (employee in employeeList) {
                        if (currentUser.organisationID == employee.organisationID) {
                            employees.add(employee)
                            val employeeButton = Button(this).apply {
                                text = employee.lastName
                                tag = employee.id
                                setOnClickListener {
                                    showEmployee(it.tag as String)
                                }
                            }
                            employeeListContainer.addView(employeeButton)
                        }
                    }
                }
            }
        }
    }

    /**
     * Opens the details page for a specific employee.
     *
     * @param employeeId The ID of the employee to view.
     */
    private fun showEmployee(employeeId: String) {
        val intent = Intent(this, ShowEmployeeActivity::class.java).apply {
            putExtra("EmployeeId", employeeId)
        }
        startActivity(intent)
    }
}
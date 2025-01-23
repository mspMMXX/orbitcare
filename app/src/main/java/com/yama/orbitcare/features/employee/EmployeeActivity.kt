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

class EmployeeActivity : AppCompatActivity() {

    private val db = FirestoreDatabase()
    private lateinit var bottomNavigation: BottomNavigationView
    private var employees = mutableListOf<Employee>()
    private lateinit var employeeListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_employee)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
        setupBottomNavigation()
        setEmployeeList()
    }

    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        employeeListContainer = findViewById(R.id.employeeListContainer)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_calendar -> {
                    // Start ClientActivity
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish() // Close current Activity
                    true
                }
                R.id.navigation_person -> {
                    // Start ClientView
                    startActivity(Intent(this, ClientActivity::class.java))
                    finish() // Close current Activity
                    true
                }
                R.id.navigation_group -> {
                    // Already in EmployeeActivity
                    true
                }
                else -> false
            }
        }

        // Set active menu button
        bottomNavigation.selectedItemId = R.id.navigation_group
    }

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

    private fun showEmployee(employeeId: String) {
        val intent = Intent(this, ShowEmployeeActivity::class.java).apply {
            putExtra("EmployeeId", employeeId)
        }
        startActivity(intent)
    }
}
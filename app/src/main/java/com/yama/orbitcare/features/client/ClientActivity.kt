package com.yama.orbitcare.features.client

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yama.orbitcare.R
import com.yama.orbitcare.features.calendar.CalendarActivity
import com.yama.orbitcare.features.employee.EmployeeActivity
import com.yama.orbitcare.features.login.SignInActivity

class ClientActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupBottomNavigation()
    }

    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_calendar -> {

                    val sharedRef = getSharedPreferences("orbitcarePref", MODE_PRIVATE)
                    val employeeId = sharedRef.getString("employeeId", "") ?: ""
                    if (employeeId.isNotEmpty()) {
                        startActivity(Intent(this, CalendarActivity::class.java).apply {
                            putExtra("employeeId", employeeId)
                        })
                    } else {
                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }
                    // Start ClientActivity
                    startActivity(Intent(this, CalendarActivity::class.java))
                    finish() // Close current Activity
                    true
                }
                R.id.navigation_group -> {
                    // Start EmployeeActivity
                    startActivity(Intent(this, EmployeeActivity::class.java))
                    finish() // Close current Activity
                    true
                }
                R.id.navigation_person -> {
                    // Already in ClientView
                    true
                }
                else -> false
            }
        }

        // Set active menu button
        bottomNavigation.selectedItemId = R.id.navigation_person
    }
}
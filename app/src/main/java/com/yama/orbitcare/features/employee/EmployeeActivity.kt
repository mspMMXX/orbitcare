package com.yama.orbitcare.features.employee

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yama.orbitcare.R
import com.yama.orbitcare.features.calendar.CalendarActivity
import com.yama.orbitcare.features.client.ClientActivity

class EmployeeActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

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
    }

    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
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
}
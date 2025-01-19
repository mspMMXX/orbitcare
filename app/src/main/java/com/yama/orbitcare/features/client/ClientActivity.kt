package com.yama.orbitcare.features.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Client
import com.yama.orbitcare.features.calendar.CalendarActivity
import com.yama.orbitcare.features.employee.EmployeeActivity
import com.yama.orbitcare.features.login.SignInActivity

class ClientActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private val db = FirestoreDatabase()
    private lateinit var clientListContainer: LinearLayout
    private var clients = mutableListOf<Client>()

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
        setClientList()
        setupBottomNavigation()
    }

    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        clientListContainer = findViewById(R.id.clientListContainer)
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

    private fun setClientList() {
        db.getAllClients { clientList ->
            if (clientList != null) {
                clients = clientList
                Log.d("Debugg", "Clients found and added to local list")
                if (clients.isNotEmpty()) {
                    for (client in clients) {
                        Log.d("Debugg", "Klient: ${client.lastName}")
                        val clientButton = Button(this).apply {
                            text = client.lastName
                            tag = client.id
                            setOnClickListener {
                                showClient(it.tag as String)
                            }
                        }
                        clientListContainer.addView(clientButton)
                    }
                }
            } else {
                Log.d("Debugg", "No Clients found in DB")
            }
        }
    }

    fun addClientButton(view: View) {
        startActivity(Intent(this, AddClientActivity::class.java))
    }

    private fun showClient(clientId: String) {
        val intent = Intent(this, ShowClientActivity::class.java).apply {
            putExtra("CLIENT_ID", clientId)
        }
        startActivity(intent)
    }
}
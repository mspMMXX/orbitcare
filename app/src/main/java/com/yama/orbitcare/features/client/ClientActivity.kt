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
import com.yama.orbitcare.features.globalUser.GlobalUser
import com.yama.orbitcare.features.login.SignInActivity

/**
 * Displays a list of clients and provides navigation options.
 * Handles client-related actions such as viewing details or adding new clients.
 */
class ClientActivity : AppCompatActivity() {

    // UI elements
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var clientListContainer: LinearLayout

    // Database instance for retrieving clients
    private val db = FirestoreDatabase()

    // Local list of clients
    private var clients = mutableListOf<Client>()

    /**
     * Called when the activity is created.
     * Sets up the UI, initializes views, loads the client list, and configures bottom navigation.
     */
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

    /**
     * Links UI components to their corresponding properties.
     */
    private fun initializeViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        clientListContainer = findViewById(R.id.clientListContainer)
    }

    /**
     * Configures the bottom navigation menu with click listeners for each menu item.
     */
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

    /**
     * Retrieves the list of clients from the database and displays them as buttons in the UI.
     * Only clients associated with the current user's organisation are displayed.
     */
    private fun setClientList() {
        val currentUser = GlobalUser.currentUser
        if (currentUser != null) {
            db.getAllClients { clientList ->
                if (clientList != null) {
                    clients = clientList
                    Log.d("Debugg", "Clients found and added to local list")
                    if (clients.isNotEmpty()) {
                        for (client in clients) {
                            if (client.orgId == currentUser.organisationID) {
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
                    }
                } else {
                    Log.d("Debugg", "No Clients found in DB")
                }
            }
        }
    }

    /**
     * Action for the "Add Client" button.
     * Navigates to AddClientActivity to create a new client.
     *
     * @param view The view that triggered the action.
     */
    fun addClientButton(view: View) {
        startActivity(Intent(this, AddClientActivity::class.java))
    }

    /**
     * Opens the details page for a specific client.
     *
     * @param clientId The ID of the client to view.
     */
    private fun showClient(clientId: String) {
        val intent = Intent(this, ShowClientActivity::class.java).apply {
            putExtra("CLIENT_ID", clientId)
        }
        startActivity(intent)
    }
}
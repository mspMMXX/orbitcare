package com.yama.orbitcare.features.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import org.w3c.dom.Text

/**
 * Displays detailed information about a specific client.
 * Retrieves client data from the database and fills the UI fields.
 */
class ShowClientActivity : AppCompatActivity() {

    // Instance of FirestoreDatabase for database operations
    private val db = FirestoreDatabase()

    // UI elements for displaying client information
    private lateinit var firstNameText: TextView
    private lateinit var lastNameText: TextView
    private lateinit var streetText: TextView
    private lateinit var plzText: TextView
    private lateinit var cityText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView

    /**
     * Called when the activity is created.
     * Sets up the UI, initializes views, and retrieves client information.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_client)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
        setClientInformation()
    }

    /**
     * Links UI components to their corresponding properties.
     */
    private fun initializeViews() {
        firstNameText = findViewById(R.id.firstNameText)
        lastNameText = findViewById(R.id.lastNameText)
        streetText = findViewById(R.id.streetText)
        plzText = findViewById(R.id.plzText)
        cityText = findViewById(R.id.cityText)
        emailText = findViewById(R.id.emailText)
        phoneText = findViewById(R.id.phoneText)
    }

    /**
     * Retrieves client information from the database based on the provided client ID
     * and populates the corresponding fields in the UI.
     */
    private fun setClientInformation() {
        val clientId = intent.getStringExtra("CLIENT_ID")
        if (clientId.isNullOrEmpty()) {
            Log.d("Debugg", "Client is Empty or null")
            } else {
            db.getClientWithFieldValue("id", clientId, onComplete = { client ->
                if (client != null) {
                    firstNameText.text = client.firstName
                    lastNameText.text = client.lastName
                    streetText.text = client.street
                    plzText.text = client.plz
                    cityText.text = client.city
                    emailText.text = client.email
                    phoneText.text = client.phone

                } else {
                    Log.d("Debugg", "Client not found for ID: $clientId")
                }
            })
        }
    }

    /**
     * Action for the exit button.
     * Navigates back to the ClientActivity.
     *
     * @param view The view that triggered the action.
     */
    fun exitButtonAction(view: View) {
        startActivity(Intent(this, ClientActivity::class.java))
    }
}
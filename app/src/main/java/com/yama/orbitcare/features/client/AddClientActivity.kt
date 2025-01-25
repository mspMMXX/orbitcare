package com.yama.orbitcare.features.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Client
import com.yama.orbitcare.features.globalUser.GlobalUser

/**
 * Activity to add a new client to the system.
 * Handles user input, saves the client to the Firestore database, and navigates between views.
 */
class AddClientActivity : AppCompatActivity() {

    // Instance of FirestoreDatabase for database operations
    private val db = FirestoreDatabase()

    // UI elements for client input
    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var streetText: EditText
    private lateinit var plzText: EditText
    private lateinit var cityText: EditText
    private lateinit var emailText: EditText
    private lateinit var phoneText: EditText

    /**
     * Called when the activity is first created.
     * Sets up the UI, initializes the views, and applies edge-to-edge design.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_client)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
    }

    /**
     * Initializes the views for user input fields.
     * Links XML elements to their corresponding properties in the class.
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
     * Action for the save button.
     * Creates a Client object from user input and saves it to the database.
     * Navigates back to the ClientActivity upon success or logs an error on failure.
     *
     * @param view The view that triggered the action.
     */
    fun saveButtonAction(view: View) {
        val currentUser = GlobalUser.currentUser
        if (currentUser != null) {
            val client = Client(
                orgId = currentUser.organisationID,
                firstName = firstNameText.text.toString(),
                lastName = lastNameText.text.toString(),
                street = streetText.text.toString(),
                plz = plzText.text.toString(),
                city = cityText.text.toString(),
                email = emailText.text.toString(),
                phone = phoneText.text.toString()
            )
            db.addClient(client, onSuccess = {
                Log.d("Debugg", "Client saved!")
                startActivity(Intent(this, ClientActivity::class.java))
            }, onFailure = {
                Log.d("Debugg", "Client not saved")
            })
        }
    }

    /**
     * Action for the cancel button.
     * Navigates back to the ClientActivity without saving any data.
     *
     * @param view The view that triggered the action.
     */
    fun cancelButtonAction(view: View) {
        startActivity(Intent(this, ClientActivity::class.java))
    }
}
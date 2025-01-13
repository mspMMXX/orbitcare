package com.yama.orbitcare.features.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Organisation

class SignUpOrganisationActivity : AppCompatActivity() {

    // UI components for organisation registration
    private lateinit var organisationIDEditText: EditText
    private lateinit var organisationNameEditText: EditText
    private lateinit var streetEditText: EditText
    private lateinit var housenumberEditText: EditText
    private lateinit var plzEditText: EditText
    private lateinit var cityEditText: EditText

    // UI components for error messages
    private lateinit var errorContainer: TextView
    private lateinit var idError: TextView

    // Flag to track if all required fields are filled
    private var textEditsFilled: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase App and log the result
        FirebaseApp.initializeApp(this)?.let {
            Log.d("FirebaseInit", "FirebaseApp initialized")
        } ?: Log.e("FirebaseInit", "FirebaseApp Init-Error")

        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Set the layout for the activity
        setContentView(R.layout.signup_organisation_activity)

        // Handle window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        organisationIDEditText = findViewById(R.id.organisationIDEditText)
        organisationNameEditText = findViewById(R.id.organisationNameEditText)
        streetEditText = findViewById(R.id.streetEditText)
        housenumberEditText = findViewById(R.id.housenumberEditText)
        plzEditText = findViewById(R.id.plzEditText)
        cityEditText = findViewById(R.id.cityEditText)
        errorContainer = findViewById(R.id.errorContainer)
        idError = findViewById(R.id.idError)
    }

    // Navigate to the SignUpEmployeeActivity
    fun employeeSignUpButton(view: View) {
        val employeeActivity = Intent(this, SignUpEmployeeActivity::class.java)
        startActivity(employeeActivity)

    }
    // Navigate to the SignInActivity
    fun signInButton(view: View) {
        val signInActivity = Intent(this, SignInActivity::class.java)
        startActivity(signInActivity)
    }

    // Handle the organisationSignUpButton click
    fun organisationSignUpButton(view:View) {
        println("OrganisationSignUpButton-clicked")
        val db = FirestoreDatabase()

        // Check if the organisationID already exists in Firestor
        db.getOrganisationWithFieldValue("organisationID", organisationIDEditText.text.toString()) { org ->
            if(org != null){
                // Display error if organisationID is already in use
                idError.visibility = View.VISIBLE
            } else {
                // Validate if all required fields are filled
                if (organisationIDEditText.text.toString().isNotEmpty() &&
                    organisationNameEditText.text.toString().isNotEmpty() &&
                    streetEditText.text.toString().isNotEmpty() &&
                    housenumberEditText.text.toString().isNotEmpty() &&
                    plzEditText.text.toString().isNotEmpty() &&
                    cityEditText.text.toString().isNotEmpty()) {
                    textEditsFilled = true
                } else {
                    textEditsFilled = false
                    errorContainer.visibility = View.VISIBLE
                }
                if (textEditsFilled) {
                    // Create a new Organisation object
                    val organisation = Organisation(
                        organisationID = organisationIDEditText.text.toString(),
                        name = organisationNameEditText.text.toString(),
                        street = streetEditText.text.toString(),
                        housenumber = housenumberEditText.text.toString(),
                        plz = plzEditText.text.toString(),
                        city = cityEditText.text.toString())

                    // Add the organisation to Firestore
                    db.addOrganisation(organisation, onSuccess = {
                        val signInActivityIntent = Intent(this, SignInActivity::class.java)
                        startActivity(signInActivityIntent)
                    }, onFailure = {
                        Log.d("Debugg", "Organisation not saved in Firestore")
                    })
                } else {
                    errorContainer.visibility = View.VISIBLE
                }
            }
        }
    }
}
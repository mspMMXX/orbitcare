package com.yama.orbitcare.features.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Employee
import com.yama.orbitcare.features.calendar.CalendarActivity
import java.security.MessageDigest

class SignInActivity : AppCompatActivity() {

    // UI components for user input and error messages
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge layout display
        enableEdgeToEdge()

        // Set the layout for this activity
        setContentView(R.layout.signin_activity)

        // Adjust UI padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInError = findViewById(R.id.signInError)
    }

    // Navigate to the SignUpOrganisationActivity
    fun organisationSignUpButton(view: View) {
        val organisationActivity = Intent(this, SignUpOrganisationActivity::class.java)
        startActivity(organisationActivity)
    }

    // Navigate to the SignUpEmployeeActivity
    fun employeeSignUpButton(view:View) {
        val employeeActivity = Intent(this, SignUpEmployeeActivity::class.java)
        startActivity(employeeActivity)
    }

    //Authenticate the user bay verifying email and password against Firestore
    fun signInButton(view: View) {
        val db = FirestoreDatabase()

        // Retrieve employee data based on the provided email
        db.getEmployeeWithFieldValue("email", emailEditText.text.toString()) { empl ->
            // Check if email matches and the provided password (after hashing) matches the stored hash
            if(empl?.email.toString() == emailEditText.text.toString() && empl?.passwordHash == pwdHash(passwordEditText.text.toString())) {

                // Navigate to the CalendarActivity
                val calendarActivity = Intent(this, CalendarActivity::class.java)
                calendarActivity.putExtra("employeeId", empl.id)
                val confirmDialog = ConfirmationDialog()
                confirmDialog.showConfirmation(this, "Hallo ${empl.firstName}.", "Willkommen bei ORBITCARE!", onComplete = {
                    startActivity(calendarActivity)
                })
            } else {
                // Display error message if authentication fails
                Log.d("Debugg", "SignINButton failed")
                signInError.visibility = View.VISIBLE
            }
        }
    }

    // Hash the input string using SHA-256 and return the hexadecimal string
    private fun pwdHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
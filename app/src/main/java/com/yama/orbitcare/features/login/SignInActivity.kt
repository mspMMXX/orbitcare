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
import com.yama.orbitcare.features.globalUser.GlobalUser
import java.security.MessageDigest

/**
 * Handles user sign-in by authenticating credentials against Firestore data.
 * Provides navigation to the sign-up screens and the main application upon successful sign-in.
 */
class SignInActivity : AppCompatActivity() {

    // Firestore database instance for user authentication
    private val db = FirestoreDatabase()

    // UI components for user input and error messages
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInError: TextView

    /**
     * Initializes the activity and sets up the UI components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signin_activity)
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

    /**
     * Navigates to the SignUpOrganisationActivity when the organisation sign-up button is clicked.
     */
    fun organisationSignUpButton(view: View) {
        val organisationActivity = Intent(this, SignUpOrganisationActivity::class.java)
        startActivity(organisationActivity)
    }

    /**
     * Navigates to the SignUpEmployeeActivity when the employee sign-up button is clicked.
     */
    fun employeeSignUpButton(view:View) {
        val employeeActivity = Intent(this, SignUpEmployeeActivity::class.java)
        startActivity(employeeActivity)
    }

    /**
     * Authenticates the user by verifying email and password against Firestore.
     * If successful, navigates to the CalendarActivity.
     * Displays an error message if authentication fails.
     */
    fun signInButton(view: View) {
        db.getEmployeeWithFieldValue("email", emailEditText.text.toString()) { empl ->
            if(empl?.email.toString() == emailEditText.text.toString() && empl?.passwordHash == pwdHash(passwordEditText.text.toString())) {
                GlobalUser.currentUser = empl
                val calendarActivity = Intent(this, CalendarActivity::class.java)
                val confirmDialog = ConfirmationDialog()
                confirmDialog.showConfirmation(this, getString(R.string.welcomeText, empl.firstName), getString(R.string.welcomeDialog), onComplete = {
                    startActivity(calendarActivity)
                })
            } else {
                Log.d("Debugg", "SignINButton failed")
                signInError.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Hashes the input string using SHA-256 and returns the resulting hexadecimal string.
     *
     * @param input The input string to be hashed.
     * @return The hashed string in hexadecimal format.
     */
    private fun pwdHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
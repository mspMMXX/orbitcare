
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
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Employee
import java.security.MessageDigest

class SignUpEmployeeActivity : AppCompatActivity() {

    // UI components for user input
    private lateinit var organisationIDEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var mailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordConfirmEditText: EditText

    // UI components for error messages
    private lateinit var idError: TextView
    private lateinit var pwdError: TextView
    private lateinit var editTextFilledError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Set the layout for this activity
        setContentView(R.layout.signup_employee_activity)

        // Handle window insets for proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        organisationIDEditText = findViewById(R.id.organisationIDEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        phoneEditText = findViewById(R.id.phoneEditText)
        mailEditText = findViewById(R.id.mailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordConfirmEditText = findViewById(R.id.passwordConfirmationEditText)
        idError = findViewById(R.id.idError)
        pwdError = findViewById(R.id.pwdError)
        editTextFilledError = findViewById(R.id.editTextFilledError)
    }

    // Navigate to the SignUpOrganisationActivity
    fun organisationSignUpButton(view: View) {
        val organisationActivity = Intent(this, SignUpOrganisationActivity::class.java)
        startActivity(organisationActivity)
    }

    // Navigate to the SignInActivity
    fun signInButton(view: View) {
        val signInActivity = Intent(this, SignInActivity::class.java)
        startActivity(signInActivity)
    }

    // Handle the SignUpButton click for registering on organisationID
    fun signUpButton(view: View){
        val db = FirestoreDatabase()
        db.getOrganisationWithFieldValue("organisationID", organisationIDEditText.text.toString()) { org ->
            if(org != null) {
                // Check if all required fields are filled
                if(organisationIDEditText.text.toString().isNotEmpty() &&
                    firstNameEditText.text.toString().isNotEmpty() &&
                    lastNameEditText.text.toString().isNotEmpty() &&
                    mailEditText.text.toString().isNotEmpty() &&
                    phoneEditText.text.toString().isNotEmpty() &&
                    passwordEditText.text.toString().isNotEmpty() &&
                    passwordConfirmEditText.text.toString().isNotEmpty()) {

                    // Check if passwords match
                    if(passwordEditText.text.toString() == passwordConfirmEditText.text.toString()) {
                        // Create Employee object with hashed password
                        val employee = Employee(
                            organisationID = organisationIDEditText.text.toString(),
                            firstName = firstNameEditText.text.toString(),
                            lastName = lastNameEditText.text.toString(),
                            phone = phoneEditText.text.toString(),
                            email = mailEditText.text.toString(),
                            passwordHash = pwdHash(passwordEditText.text.toString())
                        )

                        // Add Employee to Firestore
                        db.addEmployee(employee, onSuccess = {
                            Log.d(":Debugg", "Employee wurde erfolgreich gespeichert")
                            val signInActivityIntent = Intent(this, SignInActivity::class.java)
                            startActivity(signInActivityIntent)
                        }, onFailure = {
                            Log.d(":Debugg", "Employee konnte nicht gespeichert werden.")
                        })
                    } else {
                        // Display error if passwords do not match
                        pwdError.visibility = View.VISIBLE
                    }
                } else {
                    // Display error if required fields are emtpy
                    editTextFilledError.visibility = View.VISIBLE
                }
            } else {
                // Display error if organisation is not found
                idError.visibility = View.VISIBLE
            }
        }
    }

    // Hash a string using SHA-256
    private fun pwdHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        // Convert hashed bytes to a readable hexadecimal string
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
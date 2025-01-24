
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

/**
 * Handles the employee sign-up process, including input validation,
 * organisation verification, and user registration in Firestore.
 */
class SignUpEmployeeActivity : AppCompatActivity() {

    // Firestore database instance for employee and organisation data
    private  val db = FirestoreDatabase()

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

    /**
     * Called when the activity is created.
     * Initializes the UI components and sets up the layout.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup_employee_activity)
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

    /**
     * Navigates to the SignUpOrganisationActivity when the organisation sign-up button is clicked.
     */
    fun organisationSignUpButton(view: View) {
        val organisationActivity = Intent(this, SignUpOrganisationActivity::class.java)
        startActivity(organisationActivity)
    }

    /**
     * Navigates to the SignInActivity when the sign-in button is clicked.
     */
    fun signInButton(view: View) {
        val signInActivity = Intent(this, SignInActivity::class.java)
        startActivity(signInActivity)
    }

    /**
     * Handles the sign-up button click by validating input fields,
     * verifying the organisation ID, and registering the employee in Firestore.
     */
    fun signUpButton(view: View){
        db.getOrganisationWithFieldValue("organisationID", organisationIDEditText.text.toString()) { org ->
            if(org != null) {

                if(organisationIDEditText.text.toString().isNotEmpty() &&
                    firstNameEditText.text.toString().isNotEmpty() &&
                    lastNameEditText.text.toString().isNotEmpty() &&
                    mailEditText.text.toString().isNotEmpty() &&
                    mailEditText.text.contains("@") &&
                    phoneEditText.text.toString().isNotEmpty() &&
                    passwordEditText.text.toString().isNotEmpty() &&
                    passwordConfirmEditText.text.toString().isNotEmpty()) {

                    if(passwordEditText.text.toString() == passwordConfirmEditText.text.toString()) {
                        val employee = Employee(
                            organisationID = organisationIDEditText.text.toString(),
                            firstName = firstNameEditText.text.toString(),
                            lastName = lastNameEditText.text.toString(),
                            phone = phoneEditText.text.toString(),
                            email = mailEditText.text.toString(),
                            passwordHash = pwdHash(passwordEditText.text.toString())
                        )
                        db.addEmployee(employee, onSuccess = {
                            Log.d(":Debugg", "Employee successfully registered.")
                            val signInActivityIntent = Intent(this, SignInActivity::class.java)
                            val confirmDialog = ConfirmationDialog()
                            confirmDialog.showConfirmation(this, getString(R.string.employeeSignText), getString(R.string.employeeDialog), onComplete = {
                                startActivity(signInActivityIntent)
                            })
                        }, onFailure = {
                            Log.d(":Debugg", "Failed to register employee.")
                        })
                    } else {
                        pwdError.visibility = View.VISIBLE
                    }
                } else {
                    editTextFilledError.visibility = View.VISIBLE
                }
            } else {
                idError.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Hashes a string using SHA-256 and returns the resulting hexadecimal string.
     *
     * @param input The input string to hash.
     * @return The hashed string in hexadecimal format.
     */
    private fun pwdHash(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        // Convert hashed bytes to a readable hexadecimal string
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
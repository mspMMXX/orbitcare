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

/**
 * Handles the organisation sign-up process by validating user input
 * and adding the organisation to Firestore if all checks pass.
 */
class SignUpOrganisationActivity : AppCompatActivity() {

    // Firestore database instance for organisation data
    private val db = FirestoreDatabase()

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

    /**
     * Called when the activity is created.
     * Initializes Firebase, sets up the layout, and initializes UI components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)?.let {
            Log.d("FirebaseInit", "FirebaseApp initialized")
        } ?: Log.e("FirebaseInit", "FirebaseApp Init-Error")
        enableEdgeToEdge()
        setContentView(R.layout.signup_organisation_activity)
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

    /**
     * Navigates to the SignUpEmployeeActivity when the employee sign-up button is clicked.
     */
    fun employeeSignUpButton(view: View) {
        val employeeActivity = Intent(this, SignUpEmployeeActivity::class.java)
        startActivity(employeeActivity)

    }

    /**
     * Navigates to the SignInActivity when the sign-in button is clicked.
     */
    fun signInButton(view: View) {
        val signInActivity = Intent(this, SignInActivity::class.java)
        startActivity(signInActivity)
    }

    /**
     * Handles the organisation sign-up process by validating inputs,
     * checking for duplicate organisation IDs, and saving the organisation to Firestore.
     */
    fun organisationSignUpButton(view:View) {
        db.getOrganisationWithFieldValue("organisationID", organisationIDEditText.text.toString()) { org ->
            if(org != null){
                idError.visibility = View.VISIBLE
            } else {
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
                    val organisation = Organisation(
                        organisationID = organisationIDEditText.text.toString(),
                        name = organisationNameEditText.text.toString(),
                        street = streetEditText.text.toString(),
                        housenumber = housenumberEditText.text.toString(),
                        plz = plzEditText.text.toString(),
                        city = cityEditText.text.toString())

                    db.addOrganisation(organisation, onSuccess = {
                        val signInActivityIntent = Intent(this, SignInActivity::class.java)
                        val confirmDialog = ConfirmationDialog()
                        confirmDialog.showConfirmation(this, getString(R.string.orgSignText), getString(R.string.orgDialog), onComplete = {
                            startActivity(signInActivityIntent)
                        })
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
package com.yama.orbitcare.features.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Organisation

class SignUpOrganisationActivity : AppCompatActivity() {

    private lateinit var organisationIDEditText: EditText
    private lateinit var organisationNameEditText: EditText
    private lateinit var streetEditText: EditText
    private lateinit var housenumberEditText: EditText
    private lateinit var plzEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var errorContainer: TextView
    private lateinit var idError: TextView
    private var textEditsFilled: Boolean = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)?.let {
            Log.d("FirebaseInit", "FirebaseApp erfolgreich initialisiert")
        } ?: Log.e("FirebaseInit", "FirebaseApp konnte nicht initialisiert werden")
        enableEdgeToEdge()
        setContentView(R.layout.signup_organisation_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        organisationIDEditText = findViewById(R.id.organisationIDEditText)
        organisationNameEditText = findViewById(R.id.organisationNameEditText)
        streetEditText = findViewById(R.id.streetEditText)
        housenumberEditText = findViewById(R.id.housenumberEditText)
        plzEditText = findViewById(R.id.plzEditText)
        cityEditText = findViewById(R.id.cityEditText)
        errorContainer = findViewById(R.id.errorContainer)
        idError = findViewById(R.id.idError)
    }

    // Button - öffnet die MitarbeiterRegActivity
    fun employeeSignUpButton(view: View) {
        val employeeActivity = Intent(this, SignUpEmployeeActivity::class.java)
        startActivity(employeeActivity)

    }
    // Button - öffnet die AnmeldenActivity
    fun signInButton(view: View) {
        val signInActivity = Intent(this, SignInActivity::class.java)
        startActivity(signInActivity)
    }

    fun organisationSignUpButton(view:View) {
        println("OrganisationSignUpButton-clicked")
        val db = FirestoreDatabase()
        db.getOrganisationWithFieldValue("organisationID", organisationIDEditText.text.toString()) { org ->
            if(org != null){
                idError.visibility = View.VISIBLE
                println("ID bereits vorhanden.")
            } else {
                if (!organisationIDEditText.text.toString().isEmpty() &&
                    !organisationNameEditText.text.toString().isEmpty() &&
                    !streetEditText.text.toString().isEmpty() &&
                    !housenumberEditText.text.toString().isEmpty() &&
                    !plzEditText.text.toString().isEmpty() &&
                    !cityEditText.text.toString().isEmpty()) {
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
                        println("Organisation wurde erfolgreich hinzugefügt.")
                        val signInActivityIntent = Intent(this, SignInActivity::class.java)
                        startActivity(signInActivityIntent)
                    }, onFailure = {
                        println("Organisation konnte nicht hinzugefügt werden.")
                    })
                } else {
                    println("Bitte alle Felder ausfüllen.")
                }
            }
        }
    }
}
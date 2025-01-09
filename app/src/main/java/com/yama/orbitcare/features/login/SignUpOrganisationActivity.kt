package com.yama.orbitcare.features.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
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
        val organisation = Organisation(
            organisationID = organisationIDEditText.text.toString().toIntOrNull() ?: 0,
            name = organisationNameEditText.text.toString(),
            street = streetEditText.text.toString(),
            housenumber = housenumberEditText.text.toString(),
            plz = plzEditText.text.toString(),
            city = cityEditText.text.toString()
        )
        val db = FirestoreDatabase()
        db.addOrganisation(
            organisation,
            onSuccess = {
                Toast.makeText(this, "Organisation erfolgreich hinzugefügt", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(this, "Fehler: ${e.message}", Toast.LENGTH_SHORT).show()
            })
    }
}
package com.yama.orbitcare.features.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.features.calendar.CalendarActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signin_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInError = findViewById(R.id.signInError)
    }

    // Button - öffnet die OrganisationRegActivity
    fun organisationSignUpButton(view: View) {
        val organisationActivity = Intent(this, SignUpOrganisationActivity::class.java)
        startActivity(organisationActivity)
    }

    // Button - öffnet die MitarbeiterRegActivity
    fun employeeSignUpButton(view:View) {
        val employeeActivity = Intent(this, SignUpEmployeeActivity::class.java)
        startActivity(employeeActivity)
    }

    //Gleicht Mail und Passwort mit Firestore ab
    fun signInButton(view: View) {
        val db = FirestoreDatabase()
        db.getEmployeeWithFieldValue("email", emailEditText.text.toString()) { employee ->
            if(employee?.email.toString() == emailEditText.text.toString() && employee?.passwordHash == passwordEditText.text.toString()) {
                val calendarActivity = Intent(this, CalendarActivity::class.java)
                startActivity(calendarActivity)
            } else {
                signInError.visibility = View.VISIBLE
            }
        }
    }
}
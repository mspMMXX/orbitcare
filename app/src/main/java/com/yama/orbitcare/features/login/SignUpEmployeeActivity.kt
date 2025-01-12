
package com.yama.orbitcare.features.login

import android.content.Intent
import android.os.Bundle
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
import org.w3c.dom.Text

class SignUpEmployeeActivity : AppCompatActivity() {

    private lateinit var organisationIDEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var mailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var passwordConfirmEditText: EditText
    private lateinit var idError: TextView
    private lateinit var pwdError: TextView
    private lateinit var editTextFilledError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.signup_employee_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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

    // Button - öffnet die OrganisationRegActivity
    fun organisationSignUpButton(view: View) {
        val organisationActivity = Intent(this, SignUpOrganisationActivity::class.java)
        startActivity(organisationActivity)
    }

    // Button - öffnet die AnmeldenActivity
    fun signInButton(view: View) {
        val signInActivity = Intent(this, SignInActivity::class.java)
        startActivity(signInActivity)
    }

    fun signUpButton(view: View){
        val db = FirestoreDatabase()
        db.getOrganisationWithFieldValue("organisationID", organisationIDEditText.text.toString()) { org ->
            if(org != null) {
                if(!organisationIDEditText.text.toString().isEmpty() &&
                    !firstNameEditText.text.toString().isEmpty() &&
                    !lastNameEditText.text.toString().isEmpty() &&
                    !mailEditText.text.toString().isEmpty() &&
                    !phoneEditText.text.toString().isEmpty() &&
                    !passwordEditText.text.toString().isEmpty() &&
                    !passwordConfirmEditText.text.toString().isEmpty()) {
                    if(passwordEditText.text.toString() == passwordConfirmEditText.text.toString()) {
                        val employee = Employee(
                            organisationID = organisationIDEditText.text.toString(),
                            firstName = firstNameEditText.text.toString(),
                            lastName = lastNameEditText.text.toString(),
                            phone = phoneEditText.text.toString(),
                            email = mailEditText.text.toString(),
                            //Später Passwort-Hash BCrypt?
                            passwordHash = passwordConfirmEditText.text.toString()
                        )
                        db.addEmployee(employee, onSuccess = {
                            println("Employee wurde erfolgreich gespeichert.")
                            val signInActivityIntent = Intent(this, SignInActivity::class.java)
                            startActivity(signInActivityIntent)
                        }, onFailure = {
                            println("Employee konnte nicht gespeichert werden.")
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
}
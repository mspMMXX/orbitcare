package com.yama.orbitcare.features.employee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Employee
import com.yama.orbitcare.features.client.ClientActivity
import org.w3c.dom.Text

/**
 * Displays detailed information about a specific employee.
 * Retrieves employee data from the database and fills the UI fields.
 */
class ShowEmployeeActivity : AppCompatActivity() {

    // Instance of FirestoreDatabase for database operations
    private val db = FirestoreDatabase()

    // UI elements for displaying employee information
    private lateinit var firstNameText: TextView
    private lateinit var lastNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView

    /**
     * Called when the activity is created.
     * Sets up the UI, initializes views, and retrieves employee information.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_employee)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
        setEmployeeInformation()
    }

    /**
     * Links UI components to their corresponding properties.
     */
    private fun initializeViews() {
        firstNameText = findViewById(R.id.firstNameText)
        lastNameText = findViewById(R.id.lastNameText)
        emailText = findViewById(R.id.emailText)
        phoneText = findViewById(R.id.phoneText)
    }

    /**
     * Retrieves employee information from the database based on the provided employee ID
     * and populates the corresponding fields in the UI.
     */
    private fun setEmployeeInformation() {
        val employeeId = intent.getStringExtra("EmployeeId")
        if (employeeId.isNullOrEmpty()) {
            Log.d("Debugg", "No Emloyees in DB")
        } else {
            db.getEmployeeWithFieldValue("id", employeeId, onComplete = { empl ->
                if (empl != null) {
                    firstNameText.text = empl.firstName
                    lastNameText.text = empl.lastName
                    phoneText.text = empl.phone
                    emailText.text = empl.email
                }
            })
        }
    }

    /**
     * Action for the exit button.
     * Navigates back to the EmployeeActivity.
     *
     * @param view The view that triggered the action.
     */
    fun exitButtonAction(view: View) {
        startActivity(Intent(this, EmployeeActivity::class.java))
    }
}
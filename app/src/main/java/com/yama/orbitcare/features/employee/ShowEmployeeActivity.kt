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

class ShowEmployeeActivity : AppCompatActivity() {

    private val db = FirestoreDatabase()

    private lateinit var firstNameText: TextView
    private lateinit var lastNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView

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

    private fun initializeViews() {
        firstNameText = findViewById(R.id.firstNameText)
        lastNameText = findViewById(R.id.lastNameText)
        emailText = findViewById(R.id.emailText)
        phoneText = findViewById(R.id.phoneText)
    }

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

    fun exitButtonAction(view: View) {
        startActivity(Intent(this, EmployeeActivity::class.java))
    }
}
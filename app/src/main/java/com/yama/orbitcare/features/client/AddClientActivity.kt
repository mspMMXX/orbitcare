package com.yama.orbitcare.features.client

import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yama.orbitcare.R

class AddClientActivity : AppCompatActivity() {

    private lateinit var firstNameText: EditText
    private lateinit var lastNameText: EditText
    private lateinit var streetText: EditText
    private lateinit var plzText: EditText
    private lateinit var cityText: EditText
    private lateinit var emailText: EditText
    private lateinit var phoneText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_client)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
    }

    private fun initializeViews() {
        firstNameText = findViewById(R.id.firstNameText)
        lastNameText = findViewById(R.id.lastNameText)
        streetText = findViewById(R.id.streetText)
        plzText = findViewById(R.id.plzText)
        cityText = findViewById(R.id.cityText)
        emailText = findViewById(R.id.emailText)
        phoneText = findViewById(R.id.phoneText)
    }
}
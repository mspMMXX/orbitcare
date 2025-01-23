package com.yama.orbitcare.features.client

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
import org.w3c.dom.Text

class ShowClientActivity : AppCompatActivity() {

    private val db = FirestoreDatabase()

    private lateinit var firstNameText: TextView
    private lateinit var lastNameText: TextView
    private lateinit var streetText: TextView
    private lateinit var plzText: TextView
    private lateinit var cityText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_client)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initializeViews()
        setClientInformation()
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

    private fun setClientInformation() {
        val clientId = intent.getStringExtra("CLIENT_ID")
        if (clientId.isNullOrEmpty()) {
            Log.d("Debugg", "Client is Empty or null")
            } else {
            db.getClientWithFieldValue("id", clientId, onComplete = { client ->
                if (client != null) {
                    firstNameText.text = client.firstName
                    lastNameText.text = client.lastName
                    streetText.text = client.street
                    plzText.text = client.plz
                    cityText.text = client.city
                    emailText.text = client.email
                    phoneText.text = client.phone

                } else {
                    Log.d("Debugg", "Client not found for ID: $clientId")
                }
            })
        }

    }

    fun exitButtonAction(view: View) {
        startActivity(Intent(this, ClientActivity::class.java))
    }
}
package com.yama.orbitcare.features.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yama.orbitcare.R
import com.yama.orbitcare.data.database.FirestoreDatabase
import com.yama.orbitcare.data.models.Client

class AddClientActivity : AppCompatActivity() {

    private val db = FirestoreDatabase()

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

    fun saveButtonAction(view: View) {
        val client = Client(
            firstName = firstNameText.text.toString(),
            lastName = lastNameText.text.toString(),
            street = streetText.text.toString(),
            plz = plzText.text.toString(),
            city = cityText.text.toString(),
            email = emailText.text.toString(),
            phone = phoneText.text.toString()
            )
        db.addClient(client, onSuccess = {
            Log.d("Debugg", "Client saved!")
            startActivity(Intent(this, ClientActivity::class.java))
        }, onFailure = {
            Log.d("Debugg", "Client not saved")
        })
    }

    fun cancelButtonAction(view: View) {
        startActivity(Intent(this, ClientActivity::class.java))
    }
}
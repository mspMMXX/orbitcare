package com.yama.orbitcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MitarbeiterRegActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mitarbeiter_reg)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Button - öffnet die OrganisationRegActivity
    fun orgRegButton(view: View) {
        val orgActivity = Intent(this, OrganisationRegActivity::class.java)
        startActivity(orgActivity)
    }

    // Button - öffnet die AnmeldenActivity
    fun anmeldenButton(view: View) {
        val anmeldenActivity = Intent(this, AnmeldenActivity::class.java)
        startActivity(anmeldenActivity)
    }
}
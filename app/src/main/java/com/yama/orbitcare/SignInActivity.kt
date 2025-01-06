package com.yama.orbitcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Button - opens OrgSignUpActivity
    fun goToOrgClick(view: View) {
        val goToOrg = Intent(this, OrgSignUpActivity::class.java)
        startActivity(goToOrg)
    }

    // Button - opens EmployeeSignUpActivity
    fun goToEmplClick(view:View) {
        val goToEmpl = Intent(this, EmployeeSignUpActivity::class.java)
        startActivity(goToEmpl)
    }
}
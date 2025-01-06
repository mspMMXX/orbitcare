package com.yama.orbitcare

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OrgSignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_orgsignup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Button - opens EmployeeSignUpActivity
    fun onEmployeeSignUpClick(view: View) {
        val toEmployeeSignUp = Intent(this, EmployeeSignUpActivity::class.java)
        startActivity(toEmployeeSignUp)

    }
    // Button - opens SignInActivity
    fun goToSignInButton(view: View) {
        val toSignIn = Intent(this, SignInActivity::class.java)
        startActivity(toSignIn)
    }
}
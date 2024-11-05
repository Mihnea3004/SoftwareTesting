package com.example.softwaretesting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class Register : AppCompatActivity() {
    private lateinit var mauth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        val registerUsername = findViewById<EditText>(R.id.register_username)
        val registerPassword = findViewById<EditText>(R.id.register_password)
        val registerConfirmation = findViewById<EditText>(R.id.register_password_confirmation)
        val registerButton = findViewById<Button>(R.id.register_button)
        mauth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            val password = registerPassword.text.toString()
            val confirmation = registerConfirmation.text.toString()
            val username = registerUsername.text.toString()
            if (username.isEmpty() || password.isEmpty() || confirmation.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else
            if (password != confirmation) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                mauth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(this){
                    task ->
                    if (task.isSuccessful) {
                        val user = mauth.currentUser
                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
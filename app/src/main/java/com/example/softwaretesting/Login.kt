package com.example.softwaretesting

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var mauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mauth=FirebaseAuth.getInstance()
        setContentView(R.layout.loginactivity)
        val usernameView = findViewById<EditText>(R.id.login_username)
        val passwordView = findViewById<EditText>(R.id.login_password)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)
        loginButton.setOnClickListener {
            val username = usernameView.text.toString()
            val password = passwordView.text.toString()
            if(username.isNotEmpty() && password.isNotEmpty()) {
                mauth.signInWithEmailAndPassword(username,password).addOnCompleteListener(this) { task ->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this,"Login successful",Toast.LENGTH_SHORT).show()
                        intent.putExtra("userId", FirebaseAuth.getInstance().currentUser?.uid)
                        startActivity(Intent(this,Home::class.java))
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this,"Login failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        registerButton.setOnClickListener {
            startActivity(Intent(this,Register::class.java))
        }
    }
}
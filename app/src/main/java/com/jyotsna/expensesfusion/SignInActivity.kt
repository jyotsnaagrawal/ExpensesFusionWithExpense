package com.jyotsna.expensesfusion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jyotsna.expensesfusion.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is already authenticated, then navigate to MainActivity
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.textView.setOnClickListener {
            // Navigate to SignUpActivity when "Not Registered Yet, Sign Up!" is clicked
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Sign in with Firebase Auth
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Navigate to MainActivity after successful sign-in
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Display error message if sign-in fails
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Display error message if email or password is empty
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

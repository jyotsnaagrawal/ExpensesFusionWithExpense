package com.jyotsna.expensesfusion

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
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

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener {
            // Hide the keyboard before processing sign-in
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)

            // Run Firebase sign-in after a slight delay to allow keyboard animation to finish
            binding.root.postDelayed({
                performSignIn()
            }, 200)
        }

        binding.textView.setOnClickListener {
            Log.d("SignInActivity", "Navigating to SignUpActivity")
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun performSignIn() {
        val email = binding.emailEt.text.toString().trim()
        val pass = binding.passET.text.toString().trim()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("SignInActivity", "Sign-In Successful")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("SignInActivity", "Sign-In Failed: ${task.exception?.message}")
                    Toast.makeText(
                        this,
                        "Sign-In failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}

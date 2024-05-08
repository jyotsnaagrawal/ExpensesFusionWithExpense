package com.jyotsna.expensesfusion

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashTimeOut = 3000 // Splash screen duration in milliseconds (3 seconds)

        Handler().postDelayed({
            // Start the SignUpActivity after the timeout
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish() // Finish the SplashActivity to prevent going back to it
        }, splashTimeOut.toLong())
    }
}

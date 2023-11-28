package com.example.loginandsignup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class AnimationActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animation1)
        val lottieAnimationView: LottieAnimationView = findViewById(R.id.lottieAnimationView)
        lottieAnimationView.postDelayed({
            // Loading is complete, navigate to the main content (Activity C)
            startActivity(Intent(this, SignInActivity::class.java))
            finish() // Optional: Finish the loading screen activity
        }, 3000) // Simulated loading time: 3 seconds
    }
}

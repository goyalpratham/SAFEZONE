package com.example.loginandsignup

import android.app.Application
import android.content.Intent
import android.graphics.Color
import com.jaredrummler.cyanea.Cyanea

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Cyanea.init(this, resources)
        Cyanea.instance.edit {
            primary(Color.parseColor("#FFD700")) // Set primary color to white
            accent(Color.YELLOW)  // Set accent color to yellow
            background(Color.WHITE)  // Set background color to white
        }

        // Apply the theme
        val packageManager = packageManager
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}

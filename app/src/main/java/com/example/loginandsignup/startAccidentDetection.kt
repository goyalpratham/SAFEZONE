package com.example.loginandsignup

import android.app.AlertDialog
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlin.math.sqrt
import android.media.MediaPlayer
import java.util.logging.Handler

class startAccidentDetection : AppCompatActivity(){

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var alertDialog: AlertDialog
    private val handler = android.os.Handler()
    private val delayDuration = 20000 // 20 seconds in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_accident_detection)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        startAccidentDetection()

    }

    private fun startAccidentDetection() {

        val formattedX = intent.getDoubleExtra("formattedX", 0.0)
        val formattedY = intent.getDoubleExtra("formattedY", 0.0)
        val formattedZ = intent.getDoubleExtra("formattedZ", 0.0)


        val accelerationMagnitude = sqrt(
            formattedX.toDouble() * formattedX.toDouble() +
                    formattedY.toDouble() * formattedY.toDouble() +
                    formattedZ.toDouble() * formattedZ.toDouble()
        )
//        Toast.makeText(this, "$accelerationMagnitude", Toast.LENGTH_SHORT).show()

        val accidentThreshold = SensorManager.GRAVITY_EARTH

        if (accelerationMagnitude > accidentThreshold) {
            showAccidentDialog()
            playAlarm()
            handler.postDelayed({
                alertDialog.dismiss()
                stopAlarm()
                showHelpOnTheWayToast()
            }, delayDuration.toLong())
            Toast.makeText(this, "$accelerationMagnitude", Toast.LENGTH_SHORT).show()
        }

    }
    private fun playAlarm() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.isLooping = true // Loop the alarm sound
            mediaPlayer.start()
        }
    }

    private fun showAccidentDialog() {
        alertDialog= AlertDialog.Builder(this)
            .setTitle("Accident Detected")
            .setMessage("Are you involved in an accident?")
            .setPositiveButton("Yes") { _, _ ->
                handler.removeCallbacksAndMessages(null) // Cancel the scheduled task
                stopAlarm() // Handle user's response (they are not involved in an accident)
                showHelpOnTheWayToast()
                alertDialog.dismiss()// Handle user's response (they are not involved in an accident)
                // Handle user's response (they are involved in an accident)
            }
            .setNegativeButton("No") { _, _ ->
                stopAlarm() // Handle user's response (they are not involved in an accident)
            }
            .setCancelable(false)
            .show()
    }

    private fun stopAlarm() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepare() // Reset MediaPlayer for next use
        }
    }

    private fun showHelpOnTheWayToast() {
        Toast.makeText(this, "Help is on the way!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null) // Cancel any pending tasks
        alertDialog.dismiss()

    }


}

package com.example.loginandsignup

import android.app.AlertDialog
import android.content.ContentValues
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.sqrt

class startAccidentDetection : AppCompatActivity(){

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var alertDialog: AlertDialog
    private val handler = android.os.Handler()
    private val delayDuration = 20000 // 20 seconds in milliseconds
    val db = Firebase.firestore

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
            readData()
            sendSms()
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
//    private fun sendEmergencyMessage() {
//        val email =intent.getStringExtra("Email")
//        val emergencyContact = intent.getStringExtra("EmergencyContact")
//        val latitude = intent.getDoubleExtra("latitude", 0.0)
//        val longitude = intent.getDoubleExtra("longitude", 0.0)
//        Toast.makeText(this, "Siddharth", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "$emergencyContact", Toast.LENGTH_SHORT).show()
//        Toast.makeText(this, "$latitude", Toast.LENGTH_SHORT).show()

//        if (email != null) {
//            if (emergencyContact != null) {
//                if (email.isNotEmpty() && emergencyContact.isNotEmpty()) {
//                    val message = "Emergency! User at $latitude, $longitude has had an accident."

                    // Implement the logic to send an emergency message
                    // For example, you can use SMS APIs or an SMS gateway to send the message
                    // Here, I'm assuming a function named sendSms for illustration purposes
//                    sendSms(emergencyContact, message)
//                }
//            }
//        }
//    }
    private fun readData(){
                        db.collection("users")
                            .get()
                            .addOnSuccessListener { result ->
                                for (document in result) {
                                    val userId = document.id
                                    val email = document.getString("Email")
                                    val  emergencycontact= document.getString("EmergencyNumber")
                                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                                        Toast.makeText(this, "$emergencycontact", Toast.LENGTH_SHORT).show()
                                            val latitude = intent.getDoubleExtra("latitude", 0.0)
                                            val longitude = intent.getDoubleExtra("longitude", 0.0)
                                    Toast.makeText(this, "$latitude,$longitude", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w(ContentValues.TAG, "Error getting documents.", exception)
                            }
    }
    private fun sendSms() {

        try {
            val smsManager = SmsManager.getDefault()
//            val sentIntent = Intent("SMS_SENT")
//            val deliveredIntent = Intent("SMS_DELIVERED")
//
//            // Create pending intents
//            val sentPI = PendingIntent.getBroadcast(this, 0, sentIntent, 0)
//            val deliveredPI = PendingIntent.getBroadcast(this, 0, deliveredIntent, 0)

            // Divide the message into parts if it's too long
//            val parts = smsManager.divideMessage(message)
//
//            // Send each part
//            for (part in parts) {
                smsManager.sendTextMessage("+919872169116", null, "Hello", null, null)
//            }
//            Toast.makeText(this, "Siddharth", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle the exception, for example, show a Toast with an error message
            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
        }
    }


}

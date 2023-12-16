package com.example.loginandsignup

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.example.loginandsignup.databinding.ActivityAccelerometerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity
import java.util.*
import kotlin.math.sqrt


class AccelerometerActivity :  CyaneaAppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityAccelerometerBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var alertDialog: AlertDialog
    private val handler = android.os.Handler()
    private val delayDuration = 20000 // 20 seconds in milliseconds
    val db = Firebase.firestore
    private var isAccidentDetected = false
    private var isUserResponded = false
    private var emergencycontact: String = ""
    val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccelerometerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)


    }

    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val formattedX = String.format(Locale.US, "%.2f", x)
            val formattedY = String.format(Locale.US, "%.2f", y)
            val formattedZ = String.format(Locale.US, "%.2f", z)


            binding.tvAccelerometerX.text = "X-axis   :    $formattedX"
            binding.tvAccelerometerY.text = "Y-axis   :    $formattedY"
            binding.tvAccelerometerZ.text = "Z-axis   :    $formattedZ"

//            binding.apply {
//                btnStartAccidentDetection.setOnClickListener {
//                    val intent = Intent(this@AccelerometerActivity,startAccidentDetection::class.java)
//1
//                    val formattedXDouble = formattedX.toDoubleOrNull() ?: 0.0
//                    val formattedYDouble = formattedY.toDoubleOrNull() ?: 0.0
//                    val formattedZDouble = formattedZ.toDoubleOrNull() ?: 0.0
//
//                    intent.putExtra("formattedX", formattedXDouble)
//                    intent.putExtra("formattedY", formattedYDouble)
//                    intent.putExtra("formattedZ", formattedZDouble)
//
//
////                    intent.putExtra("formattedZ", formattedZ)
//                    startActivity(intent)
//                }
            startAccidentDetection(formattedX.toDouble(), formattedY.toDouble(), formattedZ.toDouble())
        }
    }

    private fun startAccidentDetection(formattedX: Double, formattedY: Double, formattedZ: Double) {

//        val formattedX = intent.getDoubleExtra("formattedX", 0.0)
//        val formattedY = intent.getDoubleExtra("formattedY", 0.0)
//        val formattedZ = intent.getDoubleExtra("formattedZ", 0.0)


        if(!isAccidentDetected){

            val accelerationMagnitude = sqrt(
                formattedX.toDouble() * formattedX.toDouble() +
                        formattedY.toDouble() * formattedY.toDouble() +
                        formattedZ.toDouble() * formattedZ.toDouble()
            )
//        Toast.makeText(this, "$accelerationMagnitude", Toast.LENGTH_SHORT).show()

            val accidentThreshold = 2*SensorManager.GRAVITY_EARTH

//            if (accelerationMagnitude > accidentThreshold) {
//                isAccidentDetected = true
//                showAccidentDialog()
//                playAlarm()
//                readData()
//                sendSms()
//                handler.postDelayed({
//                    alertDialog.dismiss()
//                    stopAlarm()
//                    showHelpOnTheWayToast()
//                    isAccidentDetected = false
//                }, delayDuration.toLong())
//                Toast.makeText(this, "$accelerationMagnitude", Toast.LENGTH_SHORT).show()
//            }

            if (accelerationMagnitude > accidentThreshold) {
                isAccidentDetected = true
                showAccidentDialog()
                playAlarm()
//                readData()
//                sendSms()
//                handler.postDelayed({
//                    alertDialog.dismiss()
//                    stopAlarm()
//                    showHelpOnTheWayToast()
//                    isAccidentDetected = false
//                }, delayDuration.toLong())
                startAutoSendTimer()
                Toast.makeText(this, "$accelerationMagnitude", Toast.LENGTH_SHORT).show()
            }

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
//            .setPositiveButton("Yes") { _, _ ->
//                isUserResponded = true
//                handler.removeCallbacksAndMessages(null) // Cancel the scheduled task
//                stopAlarm() // Handle user's response (they are not involved in an accident)
//                showHelpOnTheWayToast()
//                alertDialog.dismiss()// Handle user's response (they are not involved in an accident)
//                // Handle user's response (they are involved in an accident)
//                isAccidentDetected = false
//            }
            .setNegativeButton("No") { _, _ ->
                isUserResponded = true // Set flag to true when user clicks "No"
                stopAlarm()
                isAccidentDetected = false // Handle user's response (they are not involved in an accident)
            }
            .setCancelable(false)
            .show()
    }
    private fun startAutoSendTimer() {
        handler.postDelayed({
            if (!isUserResponded) {
                sendSms()
                showHelpOnTheWayToast()
                alertDialog.dismiss()
                stopAlarm()
                isAccidentDetected = false
            }
        }, delayDuration.toLong())
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

    //    private fun readData(){
//        db.collection("users")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val userId = document.id
//                    val email = document.getString("Email")
//                    val  emergencycontact= document.getString("EmergencyNumber")
//                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
//                    Toast.makeText(this, "$emergencycontact", Toast.LENGTH_SHORT).show()
////                    val latitude = intent.getDoubleExtra("latitude", 0.0)
////                    val longitude = intent.getDoubleExtra("longitude", 0.0)
////                    Toast.makeText(this, "$latitude,$longitude", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w(ContentValues.TAG, "Error getting documents.", exception)
//            }
//    }
    private fun sendSms() {
        val currentUser = auth.currentUser
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        if (currentUser != null) {
            val currentUserEmail = currentUser.email

            db.collection("users")
                .whereEqualTo("Email", currentUserEmail)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val userId = document.id
                        val email = document.getString("Email")
                        emergencycontact = document.getString("EmergencyNumber").toString()


                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    }

                    try {
                        val smsManager = SmsManager.getDefault()
                        smsManager.sendTextMessage("$emergencycontact", null, "https://www.google.com/maps?q=$latitude,$longitude", null, null)

                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Handle the exception, for example, show a Toast with an error message
                        Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
                    }
                }

            //        try {
            //            val smsManager = SmsManager.getDefault()
            //            smsManager.sendTextMessage("$emergencycontact", null, "Hello", null, null)
            //
            //        } catch (e: Exception) {
            //            e.printStackTrace()
            //            // Handle the exception, for example, show a Toast with an error message
            //            Toast.makeText(this, "Failed to send SMS", Toast.LENGTH_SHORT).show()
            //        }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }
}

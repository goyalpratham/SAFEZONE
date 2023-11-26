package com.example.loginandsignup

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import com.example.loginandsignup.databinding.ActivityAccelerometerBinding
import java.util.*


class AccelerometerActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityAccelerometerBinding
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccelerometerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)



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

            binding.apply {
                btnStartAccidentDetection.setOnClickListener {
                    val intent = Intent(this@AccelerometerActivity,startAccidentDetection::class.java)

                    val formattedXDouble = formattedX.toDoubleOrNull() ?: 0.0
                    val formattedYDouble = formattedY.toDoubleOrNull() ?: 0.0
                    val formattedZDouble = formattedZ.toDoubleOrNull() ?: 0.0

                    intent.putExtra("formattedX", formattedXDouble)
                    intent.putExtra("formattedY", formattedYDouble)
                    intent.putExtra("formattedZ", formattedZDouble)


//                    intent.putExtra("formattedZ", formattedZ)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this example
    }
}

package com.example.loginandsignup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.loginandsignup.databinding.ActivityMainBinding
import com.jaredrummler.cyanea.app.CyaneaAppCompatActivity

class MainActivity :  CyaneaAppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private var service: Intent? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val locationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val latitude = intent?.getDoubleExtra("latitude", 0.0)
            val longitude = intent?.getDoubleExtra("longitude", 0.0)
            binding.apply {
                btnShowOnMap.setOnClickListener {
                    val intent = Intent(this@MainActivity, MapsActivity::class.java)
                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)
                    startActivity(intent)
                }
                btnAccelerometerReadings.setOnClickListener {
                    val intent = Intent(this@MainActivity, AccelerometerActivity::class.java)
                    intent.putExtra("latitude", latitude)
                    intent.putExtra("longitude", longitude)
                    startActivity(intent)
                }
            }
//            binding.tvLatitude.text = "Latitude -> $latitude"
//            binding.tvLongitude.text = "Longitude -> $longitude"
        }
    }


    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

            }
        }

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }

                }
                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

                }
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = Intent(this,LocationService::class.java)

        binding.apply {
            btnStartLocationTracking.setOnClickListener {
                checkPermissions()
            }
            btnRemoveLocationTracking.setOnClickListener {
                stopService(service)
//                Toast.makeText(this,"Location Tracking Stopped",Toast.LENGTH_SHORT)
            }
        }

    }

    private fun sendLocationBroadcast(latitude: Double?, longitude: Double?) {
        val intent = Intent("location_update")
        intent.putExtra("latitude", latitude)
        intent.putExtra("longitude", longitude)
        sendBroadcast(intent)
    }



    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                Toast.makeText(this,"Location Tracking Started",Toast.LENGTH_SHORT).show()
            }else{
                startService(service)
                Toast.makeText(this,"Location Tracking Started",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(locationUpdateReceiver, IntentFilter("location_update"))
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(locationUpdateReceiver)
    }
}

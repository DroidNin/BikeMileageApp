package com.example.bikemileageconverterapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("BikeMileageHistory", Context.MODE_PRIVATE)

        val tvPrevKm: EditText = findViewById(R.id.edt_prev_km)
        val tvCurrentKm: EditText = findViewById(R.id.edt_current_km)
        val tvFuelCapacity: EditText = findViewById(R.id.edt_fuel_capacity)
        val btnCalculate: Button = findViewById(R.id.btn)
        val tvResult: TextView = findViewById(R.id.tv_result)

        loadHistory(tvPrevKm, tvCurrentKm, tvFuelCapacity)

        btnCalculate.setOnClickListener {
            val prevKmStr = tvPrevKm.text.toString()
            val currentKmStr = tvCurrentKm.text.toString()
            val fuelCapacityStr = tvFuelCapacity.text.toString()

            if (prevKmStr.isEmpty() || currentKmStr.isEmpty() || fuelCapacityStr.isEmpty()) {
                tvResult.text = "Please fill in all fields"
                return@setOnClickListener
            }

            val prevKm = prevKmStr.toDouble()
            val currentKm = currentKmStr.toDouble()
            val fuelCapacity = fuelCapacityStr.toDouble()

            if (prevKm >= currentKm) {
                tvResult.text = "Current Kilometers should be greater than Previous Kilometers"
                return@setOnClickListener
            }

            val distance = currentKm - prevKm
            val mileage = distance / fuelCapacity

            tvResult.text = "Your bike's mileage is $mileage km/L"

            saveHistory(prevKm, currentKm, fuelCapacity)
        }
    }

    private fun saveHistory(prevKm: Double, currentKm: Double, fuelCapacity: Double) {
        val editor = sharedPreferences.edit()
        val history = "${prevKm},${currentKm},${fuelCapacity}"

        val currentHistory = sharedPreferences.getString("history", "")
        val newHistory = if (currentHistory.isNullOrEmpty()) {
            history
        } else {
            "$currentHistory;$history"
        }

        editor.putString("history", newHistory)
        editor.apply()
    }

    private fun loadHistory(tvPrevKm: EditText, tvCurrentKm: EditText, tvFuelCapacity: EditText) {
        val history = sharedPreferences.getString("history", "")

        if (!history.isNullOrEmpty()) {
            val parts = history.split(";")
            val lastEntry = parts.lastOrNull()?.split(",")

            lastEntry?.let {
                if (it.size == 3) {
                    tvPrevKm.setText(it[0])
                    tvCurrentKm.setText(it[1])
                    tvFuelCapacity.setText(it[2])
                }
            }
        }
    }
}
package com.example.getgyroscopedata

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GyroscopeViewModel(applicationContext: Context) : ViewModel(), SensorEventListener {

    private val sensorManager = applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscopeSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _gyroscopeData = MutableStateFlow(Triple(0f, 0f, 0f))
    val gyroscopeData: StateFlow<Triple<Float, Float, Float>> = _gyroscopeData

    private val _isListening = mutableStateOf(false)
    val isListening: State<Boolean> = _isListening


    init {
        startListening()
    }

    fun startListening() {
        gyroscopeSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            _isListening.value = true
            Log.d("GyroscopeViewModel", "Started listening to gyroscope")
        } ?: run {
            Log.e("GyroscopeViewModel", "Gyroscope sensor not available on this device.")
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
        _isListening.value = false
        Log.d("GyroscopeViewModel", "Stopped listening to gyroscope")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            _gyroscopeData.value = Triple(x, y, z)
            // You can process or log the gyroscope data here
            // Log.d("GyroscopeData", "X: $x, Y: $y, Z: $z")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not typically used for gyroscope
        Log.d("GyroscopeViewModel", "Accuracy changed for sensor: $sensor, accuracy: $accuracy")
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}

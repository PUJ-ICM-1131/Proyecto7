package com.example.proyecto.ui.components

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt

data class SensorDataState(
    val azimuthDegrees: Float = 0f,
    val cardinalDirection: String = "N",
    val speedWarning: Boolean = false,
    val accelerationMagnitude: Float = 0f
)

@Composable
fun rememberSensorDataState(): SensorDataState {
    val context = LocalContext.current
    var sensorState by remember { mutableStateOf(SensorDataState()) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager

        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        var gravity: FloatArray? = null
        var geomagnetic: FloatArray? = null

        var lastAccelTime = System.currentTimeMillis()
        var currentSpeedEst = 0f

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return

                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        gravity = event.values.clone()

                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        val mag = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

                        val now = System.currentTimeMillis()
                        val dt = (now - lastAccelTime) / 1000f
                        lastAccelTime = now

                        val accelMinusGravity = Math.abs(mag - SensorManager.GRAVITY_EARTH)
                        if (accelMinusGravity > 4.5f) {
                            currentSpeedEst += accelMinusGravity * dt * 3.6f
                        } else {
                            currentSpeedEst = (currentSpeedEst - 1.0f).coerceAtLeast(0f)
                        }

                        val warning = accelMinusGravity > 8.0f || currentSpeedEst > 25.0f

                        sensorState = sensorState.copy(
                            accelerationMagnitude = mag,
                            speedWarning = warning
                        )
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        geomagnetic = event.values.clone()
                    }
                }

                val g = gravity
                val m = geomagnetic
                if (g != null && m != null) {
                    val R = FloatArray(9)
                    val I = FloatArray(9)
                    if (SensorManager.getRotationMatrix(R, I, g, m)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(R, orientation)
                        var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        if (azimuth < 0) {
                            azimuth += 360f
                        }

                        val directionName = when (azimuth) {
                            in 22.5f..67.5f -> "NE (Noreste)"
                            in 67.5f..112.5f -> "E (Este)"
                            in 112.5f..157.5f -> "SE (Sureste)"
                            in 157.5f..202.5f -> "S (Sur)"
                            in 202.5f..247.5f -> "SO (Suroeste)"
                            in 247.5f..292.5f -> "O (Oeste)"
                            in 292.5f..337.5f -> "NO (Noroeste)"
                            else -> "N (Norte)"
                        }

                        sensorState = sensorState.copy(
                            azimuthDegrees = azimuth,
                            cardinalDirection = directionName
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (sensorManager != null) {
            accelerometer?.let {
                sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
            }
            magnetometer?.let {
                sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
            }
        }

        onDispose {
            sensorManager?.unregisterListener(sensorEventListener)
        }
    }

    return sensorState
}

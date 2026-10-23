package com.example.proyecto.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

data class UserCoordinates(
    val latitude: Double,
    val longitude: Double
)

@SuppressLint("MissingPermission")
@Composable
fun rememberUserCoordinates(): UserCoordinates? {
    val context = LocalContext.current

    var coordinates by remember {
        mutableStateOf<UserCoordinates?>(null)
    }

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            val activity = context as? Activity
            if (activity != null) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1001
                )
            }
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            try {
                val client = LocationServices.getFusedLocationProviderClient(context)
                val location = client.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    null
                ).await()

                if (location != null) {
                    coordinates = UserCoordinates(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
            } catch (_: Exception) {
                coordinates = null
            }
        }
    }

    return coordinates
}

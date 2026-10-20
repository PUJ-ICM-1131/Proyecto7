package com.example.proyecto.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyecto.R
import com.example.proyecto.data.repository.TestRepository
import com.example.proyecto.ui.components.rememberSensorDataState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import com.example.proyecto.ui.components.MapboxStreets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    testId: String,
    testRepository: TestRepository,
    onBack: () -> Unit
) {
    val todosLosTests by testRepository.todosLosTests.collectAsState()
    val test = todosLosTests.find { it.id == testId } ?: todosLosTests.firstOrNull()

    val context = LocalContext.current
    val sensorData = rememberSensorDataState()
    var simulatedFastSpeed by remember { mutableStateOf(false) }

    val effectiveWarning = sensorData.speedWarning || simulatedFastSpeed

    val userLat = 4.6097
    val userLng = -74.0817
    val destLat = test?.latitud ?: 4.6698
    val destLng = test?.longitud ?: -74.0532



    if (test == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.test_not_found))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(test.nombre, fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Mapa Nativo OSM/Mapbox
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(MapboxStreets)
                        setMultiTouchControls(true)
                        controller.setZoom(14.5)

                        val userPoint = GeoPoint(userLat, userLng)
                        val destPoint = GeoPoint(destLat, destLng)
                        val midPoint = GeoPoint((userLat + destLat) / 2, (userLng + destLng) / 2)

                        controller.setCenter(midPoint)

                        // Marcador Usuario
                        val uMarker = Marker(this).apply {
                            position = userPoint
                            title = "Tu Ubicación (Origen)"
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        overlays.add(uMarker)

                        // Marcador Destino
                        val dMarker = Marker(this).apply {
                            position = destPoint
                            title = test.nombre
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        overlays.add(dMarker)

                        // Línea de Ruta Punteada por Calles
                        val routePolyline = Polyline().apply {
                            outlinePaint.color = android.graphics.Color.parseColor("#1565C0")
                            outlinePaint.strokeWidth = 12f
                            addPoint(userPoint)
                            addPoint(GeoPoint((userLat * 2 + destLat) / 3, userLng))
                            addPoint(GeoPoint((userLat + destLat * 2) / 3, (userLng + destLng) / 2))
                            addPoint(destPoint)
                        }
                        overlays.add(routePolyline)

                        invalidate()
                    }
                },
                update = { it.invalidate() },
                onRelease = { it.onDetach() },
                modifier = Modifier.fillMaxSize()
            )

            // Brújula Flotante (Magnetómetro)
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(56.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Brújula",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(sensorData.azimuthDegrees)
                    )
                    Text(
                        text = "${sensorData.azimuthDegrees.toInt()}°",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                }
            }

            // Alerta de Acelerómetro (Conducción Rápida)
            AnimatedVisibility(
                visible = effectiveWarning,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp, start = 80.dp, end = 20.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFD32F2F),
                    contentColor = Color.White,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Alerta! No uses el celular mientras conduces.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            /*
            // Panel Inferior de Datos de Navegación
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFF1565C0))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Ruta hacia: ${test.direccion}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    /*
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CompassCalibration, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.label_orientation, sensorData.cardinalDirection),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.label_distance),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { simulatedFastSpeed = !simulatedFastSpeed },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (simulatedFastSpeed) stringResource(R.string.btn_stop_simulation) else stringResource(R.string.btn_test_speed_warning))
                    }

                     */


                }


            }

             */
        }
    }
}

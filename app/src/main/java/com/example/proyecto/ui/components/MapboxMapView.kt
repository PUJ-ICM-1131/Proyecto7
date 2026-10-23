package com.example.proyecto.ui.components
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyecto.R
import com.example.proyecto.data.model.TestModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

const val MAPBOX_ACCESS_TOKEN: String = ""

@Composable
fun MapboxMapView(
    test: TestModel,
    sensorData: SensorDataState,
    onOpenFullMap: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var simulatedFastSpeed by remember {
        mutableStateOf(false)
    }

    val effectiveWarning =
        sensorData.speedWarning || simulatedFastSpeed

    val userCoordinates = rememberUserCoordinates()

    val userLat: Double? = userCoordinates?.latitude
    val userLng: Double? = userCoordinates?.longitude

    val destLat = test.latitud
    val destLng = test.longitud


    var routeResult by remember {
        mutableStateOf<RouteResult?>(null)
    }

    LaunchedEffect(userLat, userLng, destLat, destLng) {
        routeResult = null

        if (userLat != null && userLng != null) {
            routeResult = fetchMapboxRoute(
                originLat = userLat,
                originLng = userLng,
                destLat = destLat,
                destLng = destLng
            )
        }
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE3F2FD))
            ) {

                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {

                            setTileSource(MapboxStreets)
                            setMultiTouchControls(true)
                            val uPoint=GeoPoint(0.0,0.0)
                            val dPoint=GeoPoint(0.0,0.0)
                            controller.setZoom(13.8)
                            if (userLat != null && userLng != null) {
                                val uPoint = GeoPoint(
                                    userLat,
                                    userLng
                                )

                                val dPoint = GeoPoint(
                                    destLat,
                                    destLng
                                )
                            }


                            if (userLat != null && userLng != null) {
                                controller.setCenter(
                                    GeoPoint(
                                        (userLat + destLat) / 2,
                                        (userLng + destLng) / 2
                                    )
                                )
                            } else {
                                controller.setCenter(
                                    GeoPoint(destLat, destLng)
                                )
                            }

                            val uMarker = Marker(this).apply {
                                position = uPoint
                                title = "Tu Ubicación (Origen)"

                                setAnchor(
                                    Marker.ANCHOR_CENTER,
                                    Marker.ANCHOR_BOTTOM
                                )
                            }

                            overlays.add(uMarker)

                            val dMarker = Marker(this).apply {
                                position = dPoint
                                title = test.nombre

                                setAnchor(
                                    Marker.ANCHOR_CENTER,
                                    Marker.ANCHOR_BOTTOM
                                )
                            }

                            overlays.add(dMarker)

                            invalidate()
                        }
                    },
                    update = { mapView ->

                        mapView.overlays.removeAll {
                            it is Polyline
                        }

                        routeResult?.let { result ->

                            if (result.points.size >= 2) {
                                val line = Polyline().apply {
                                    outlinePaint.color =
                                        android.graphics.Color.parseColor("#1565C0")

                                    outlinePaint.strokeWidth = 10f

                                    result.points.forEach { point ->
                                        addPoint(point)
                                    }
                                }

                                mapView.overlays.add(line)
                            }
                        }

                        mapView.invalidate()
                    },
                    onRelease = { mapView ->
                        mapView.onDetach()
                    },
                    modifier = Modifier.fillMaxSize()
                )

                Column(modifier=Modifier.align(Alignment.Center)) {
                    AnimatedVisibility(
                        visible = effectiveWarning,
                        modifier = Modifier
                            .padding(20.dp),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {

                        Surface(
                            color = Color(0xFFD32F2F)
                                .copy(alpha = 0.94f),
                            contentColor = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 12.dp
                                ),
                                verticalAlignment = Alignment.Top
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = stringResource(
                                        R.string.warning_title
                                    ),
                                    modifier = Modifier.size(28.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(12.dp)
                                )

                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {

                                    Text(
                                        text = stringResource(
                                            R.string.warning_title
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )

                                    Text(
                                        text = stringResource(
                                            R.string.warning_body
                                        ),
                                        fontSize = 12.sp
                                    )

                                }
                            }
                        }
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .size(52.dp)
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(
                                    sensorData.azimuthDegrees
                                )
                        )

                        Text(
                            text = "${sensorData.azimuthDegrees.toInt()}°",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                    }
                }

                onOpenFullMap?.let { onOpen ->

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                    ) {

                        Button(
                            onClick = onOpen,
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                    MaterialTheme.colorScheme.primary
                            ),
                            shape = CircleShape,
                            modifier = Modifier.size(44.dp),
                            contentPadding =
                                androidx.compose.foundation.layout
                                    .PaddingValues(0.dp)
                        ) {

                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Pantalla Completa",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

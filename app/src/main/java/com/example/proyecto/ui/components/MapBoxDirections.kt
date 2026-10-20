package com.example.proyecto.ui.components

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.net.HttpURLConnection
import java.net.URL

data class RouteResult(
    val points: List<GeoPoint>,
    val distanceMeters: Double,
    val durationSeconds: Double
)

suspend fun fetchMapboxRoute(
    originLat: Double?,
    originLng: Double?,
    destLat: Double,
    destLng: Double
): RouteResult? = withContext(Dispatchers.IO) {

    val token = MAPBOX_ACCESS_TOKEN

    val urlString =
        "https://api.mapbox.com/directions/v5/mapbox/driving/" +
                "$originLng,$originLat;$destLng,$destLat" +
                "?alternatives=false&overview=full&geometries=geojson" +
                "&access_token=$token"

    var connection: HttpURLConnection? = null

    try {
        connection = URL(urlString)
            .openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            return@withContext null
        }

        val response = connection.inputStream
            .bufferedReader()
            .use { it.readText() }

        val json = JSONObject(response)

        if (json.optString("code") != "Ok") {
            return@withContext null
        }

        val routes = json.getJSONArray("routes")

        if (routes.length() == 0) {
            return@withContext null
        }

        val route = routes.getJSONObject(0)
        val coordinates = route
            .getJSONObject("geometry")
            .getJSONArray("coordinates")

        val points = mutableListOf<GeoPoint>()

        for (i in 0 until coordinates.length()) {
            val coordinate = coordinates.getJSONArray(i)

            // GeoJSON entrega [longitud, latitud].
            val longitude = coordinate.getDouble(0)
            val latitude = coordinate.getDouble(1)

            points.add(GeoPoint(latitude, longitude))
        }

        RouteResult(
            points = points,
            distanceMeters = route.getDouble("distance"),
            durationSeconds = route.getDouble("duration")
        )
    } catch (e: Exception) {
        null
    } finally {
        connection?.disconnect()
    }
}
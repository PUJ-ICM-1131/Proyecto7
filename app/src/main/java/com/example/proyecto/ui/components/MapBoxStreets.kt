package com.example.proyecto.ui.components


import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

val MapboxStreets = object : OnlineTileSourceBase(
    "MapboxStreetsV12",
    0, 19, 256, ".png",
    arrayOf("https://api.mapbox.com/styles/v1/mapbox/streets-v12/tiles/256/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl +
                MapTileIndex.getZoom(pMapTileIndex) + "/" +
                MapTileIndex.getX(pMapTileIndex) + "/" +
                MapTileIndex.getY(pMapTileIndex) +
                "?access_token=$MAPBOX_ACCESS_TOKEN"
    }
}
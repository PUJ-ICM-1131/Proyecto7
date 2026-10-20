package com.example.proyecto.data.model

data class TestModel(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val recompensa: String,
    val criteriosAplicacion: String,
    val direccion: String,
    val tipoProducto: String,
    val latitud: Double = 4.6097,
    val longitud: Double = -74.0817,
    val cumpleRequisitos: Boolean = true,
    val resenas: List<Resena> = emptyList()
)

package com.example.proyecto.data.model

data class InscripcionTest(
    val id: String,
    val test: TestModel,
    val estadoRecompensa: String = "En curso",
    val evidenciasUris: List<String> = emptyList(),
    val completado: Boolean = false
)

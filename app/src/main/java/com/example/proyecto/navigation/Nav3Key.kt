package com.example.proyecto.navigation

import kotlinx.serialization.Serializable

sealed interface Nav3Key {
    @Serializable
    data object Login : Nav3Key

    @Serializable
    data object Register : Nav3Key

    @Serializable
    data object UserMain : Nav3Key

    @Serializable
    data object EmpresaWelcome : Nav3Key

    @Serializable
    data class TestDetail(val testId: String) : Nav3Key

    @Serializable
    data class MapDetail(val testId: String) : Nav3Key
}

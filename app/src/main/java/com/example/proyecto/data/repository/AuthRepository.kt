package com.example.proyecto.data.repository

import com.example.proyecto.data.model.Empresa
import com.example.proyecto.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TipoUsuario {
    USUARIO,
    EMPRESA
}

data class AuthState(
    val isAuthenticated: Boolean = false,
    val tipoUsuario: TipoUsuario = TipoUsuario.USUARIO,
    val usuario: Usuario? = null,
    val empresa: Empresa? = null
)

interface AuthRepository {
    val authState: StateFlow<AuthState>
    suspend fun login(correoOUser: String, contrasena: String, tipo: TipoUsuario): Result<AuthState>
    suspend fun registrarUsuario(usuario: Usuario, contrasena: String): Result<AuthState>
    suspend fun registrarEmpresa(empresa: Empresa, contrasena: String): Result<AuthState>
    fun actualizarPerfilUsuario(usuarioActualizado: Usuario)
    fun cerrarSesion()
}

class MockAuthRepository : AuthRepository {

    private val usuarioPrueba = Usuario(
        id = "u_prueba",
        nombre = "Juan Pérez (Prueba)",
        edad = 25,
        fechaNacimiento = "15/08/1999",
        estrato = 3,
        direccion = "Calle 123 #45-67, Bogotá",
        correo = "prueba@correo.com",
        telefono = "3001234567"
    )

    private val empresaPrueba = Empresa(
        id = "e_prueba",
        nombre = "Tech Testing Corp",
        numeroContacto = "3109876543",
        correo = "empresa@tech.com",
        rut = "900123456-1",
        direccion = "Carrera 7 #100-20, Bogotá"
    )

    private val _authState = MutableStateFlow(AuthState())
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun login(correoOUser: String, contrasena: String, tipo: TipoUsuario): Result<AuthState> {
        val userClean = correoOUser.trim().lowercase()
        val passClean = contrasena.trim()

        if ((userClean == "prueba" || userClean == "prueba@correo.com" || userClean.isNotEmpty()) && passClean == "1234") {
            val state = if (tipo == TipoUsuario.USUARIO) {
                AuthState(
                    isAuthenticated = true,
                    tipoUsuario = TipoUsuario.USUARIO,
                    usuario = usuarioPrueba.copy(
                        correo = if (userClean.contains("@")) userClean else usuarioPrueba.correo
                    )
                )
            } else {
                AuthState(
                    isAuthenticated = true,
                    tipoUsuario = TipoUsuario.EMPRESA,
                    empresa = empresaPrueba.copy(
                        correo = if (userClean.contains("@")) userClean else empresaPrueba.correo
                    )
                )
            }
            _authState.value = state
            return Result.success(state)
        } else if (passClean.isNotEmpty() && userClean.isNotEmpty()) {
            val state = if (tipo == TipoUsuario.USUARIO) {
                AuthState(
                    isAuthenticated = true,
                    tipoUsuario = TipoUsuario.USUARIO,
                    usuario = usuarioPrueba.copy(
                        nombre = correoOUser,
                        correo = if (userClean.contains("@")) userClean else "$userClean@correo.com"
                    )
                )
            } else {
                AuthState(
                    isAuthenticated = true,
                    tipoUsuario = TipoUsuario.EMPRESA,
                    empresa = empresaPrueba.copy(
                        nombre = correoOUser,
                        correo = if (userClean.contains("@")) userClean else "$userClean@empresa.com"
                    )
                )
            }
            _authState.value = state
            return Result.success(state)
        } else {
            return Result.failure(Exception("Credenciales inválidas. Usa usuario 'prueba' y contraseña '1234'."))
        }
    }

    override suspend fun registrarUsuario(usuario: Usuario, contrasena: String): Result<AuthState> {
        val state = AuthState(
            isAuthenticated = true,
            tipoUsuario = TipoUsuario.USUARIO,
            usuario = usuario
        )
        _authState.value = state
        return Result.success(state)
    }

    override suspend fun registrarEmpresa(empresa: Empresa, contrasena: String): Result<AuthState> {
        val state = AuthState(
            isAuthenticated = true,
            tipoUsuario = TipoUsuario.EMPRESA,
            empresa = empresa
        )
        _authState.value = state
        return Result.success(state)
    }

    override fun actualizarPerfilUsuario(usuarioActualizado: Usuario) {
        _authState.value = _authState.value.copy(usuario = usuarioActualizado)
    }

    override fun cerrarSesion() {
        _authState.value = AuthState()
    }
}

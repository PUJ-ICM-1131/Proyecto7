package com.example.proyecto.data.repository

import com.example.proyecto.data.model.Empresa
import com.example.proyecto.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    private val _authState = MutableStateFlow(AuthState())
    override val authState: StateFlow<AuthState> =
        _authState.asStateFlow()

    override suspend fun login(
        correoOUser: String,
        contrasena: String,
        tipo: TipoUsuario
    ): Result<AuthState> {
        return try {
            require(correoOUser.contains("@")) {
                "Ingresa un correo electrónico válido."
            }

            val result = firebaseAuth
                .signInWithEmailAndPassword(
                    correoOUser.trim(),
                    contrasena
                )
                .await()

            val firebaseUser = result.user
                ?: throw Exception("No se pudo obtener el usuario.")

            val state = AuthState(
                isAuthenticated = true,
                tipoUsuario = tipo,
                usuario = if (tipo == TipoUsuario.USUARIO) {
                    Usuario(
                        id = firebaseUser.uid,
                        nombre = firebaseUser.displayName ?: "",
                        edad = 0,
                        fechaNacimiento = "",
                        estrato = 0,
                        direccion = "",
                        correo = firebaseUser.email ?: correoOUser,
                        telefono = ""
                    )
                } else null,
                empresa = if (tipo == TipoUsuario.EMPRESA) {
                    Empresa(
                        id = firebaseUser.uid,
                        nombre = firebaseUser.displayName ?: "",
                        numeroContacto = "",
                        correo = firebaseUser.email ?: correoOUser,
                        rut = "",
                        direccion = ""
                    )
                } else null
            )

            _authState.value = state
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarUsuario(
        usuario: Usuario,
        contrasena: String
    ): Result<AuthState> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(
                    usuario.correo.trim(),
                    contrasena
                )
                .await()

            val firebaseUser = result.user
                ?: throw Exception("No se pudo crear la cuenta.")

            firebaseUser.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(usuario.nombre)
                    .build()
            ).await()

            val usuarioGuardado = usuario.copy(id = firebaseUser.uid)
            val state = AuthState(
                isAuthenticated = true,
                tipoUsuario = TipoUsuario.USUARIO,
                usuario = usuarioGuardado
            )

            _authState.value = state
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarEmpresa(
        empresa: Empresa,
        contrasena: String
    ): Result<AuthState> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(
                    empresa.correo.trim(),
                    contrasena
                )
                .await()

            val firebaseUser = result.user
                ?: throw Exception("No se pudo crear la cuenta.")

            firebaseUser.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest
                    .Builder()
                    .setDisplayName(empresa.nombre)
                    .build()
            ).await()

            val empresaGuardada = empresa.copy(id = firebaseUser.uid)
            val state = AuthState(
                isAuthenticated = true,
                tipoUsuario = TipoUsuario.EMPRESA,
                empresa = empresaGuardada
            )

            _authState.value = state
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun actualizarPerfilUsuario(usuarioActualizado: Usuario) {
        _authState.value = _authState.value.copy(
            usuario = usuarioActualizado
        )
    }

    override fun cerrarSesion() {
        firebaseAuth.signOut()
        _authState.value = AuthState()
    }
}

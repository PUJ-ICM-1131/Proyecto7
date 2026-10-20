package com.example.proyecto.ui.screens.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.R
import com.example.proyecto.data.model.Usuario
import com.example.proyecto.data.repository.AuthRepository

@Composable
fun PerfilTab(
    authRepository: AuthRepository,
    onLogout: () -> Unit
) {
    val authState by authRepository.authState.collectAsState()
    val usuario = authState.usuario ?: Usuario()

    var nombre by remember(usuario) { mutableStateOf(usuario.nombre) }
    var edad by remember(usuario) { mutableStateOf(usuario.edad.toString()) }
    var fechaNacimiento by remember(usuario) { mutableStateOf(usuario.fechaNacimiento) }
    var estrato by remember(usuario) { mutableFloatStateOf(usuario.estrato.toFloat()) }
    var direccion by remember(usuario) { mutableStateOf(usuario.direccion) }
    var correo by remember(usuario) { mutableStateOf(usuario.correo) }
    var telefono by remember(usuario) { mutableStateOf(usuario.telefono) }

    var mensajeExito by remember { mutableStateOf(false) }

    val defaultTitle = stringResource(R.string.profile_title)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .size(76.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            )
        }

        Text(
            text = nombre.ifBlank { defaultTitle },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.profile_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = {
                        nombre = it
                        mensajeExito = false
                    },
                    label = { Text(stringResource(R.string.field_full_name)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = edad,
                        onValueChange = {
                            edad = it
                            mensajeExito = false
                        },
                        label = { Text(stringResource(R.string.field_age)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = fechaNacimiento,
                        onValueChange = {
                            fechaNacimiento = it
                            mensajeExito = false
                        },
                        label = { Text(stringResource(R.string.field_birth_date)) },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(start = 4.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = stringResource(R.string.field_stratum, estrato.toInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = estrato,
                        onValueChange = {
                            estrato = it
                            mensajeExito = false
                        },
                        valueRange = 1f..6f,
                        steps = 4
                    )
                }

                OutlinedTextField(
                    value = direccion,
                    onValueChange = {
                        direccion = it
                        mensajeExito = false
                    },
                    label = { Text(stringResource(R.string.field_address)) },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        mensajeExito = false
                    },
                    label = { Text(stringResource(R.string.field_email)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = telefono,
                    onValueChange = {
                        telefono = it
                        mensajeExito = false
                    },
                    label = { Text(stringResource(R.string.field_phone)) },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                if (mensajeExito) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.profile_updated_success),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val usuarioActualizado = usuario.copy(
                            nombre = nombre,
                            edad = edad.toIntOrNull() ?: usuario.edad,
                            fechaNacimiento = fechaNacimiento,
                            estrato = estrato.toInt(),
                            direccion = direccion,
                            correo = correo,
                            telefono = telefono
                        )
                        authRepository.actualizarPerfilUsuario(usuarioActualizado)
                        mensajeExito = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_save_changes), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                authRepository.cerrarSesion()
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.btn_close_session), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

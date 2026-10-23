package com.example.proyecto.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto.R
import com.example.proyecto.data.model.Empresa
import com.example.proyecto.data.model.Usuario
import com.example.proyecto.data.repository.AuthRepository
import com.example.proyecto.data.repository.TipoUsuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authRepository: AuthRepository,
    onBackToLogin: () -> Unit,
    onRegisterSuccessUsuario: () -> Unit,
    onRegisterSuccessEmpresa: () -> Unit
) {
    var tipoUsuario by remember { mutableStateOf(TipoUsuario.USUARIO) }


    var nombreUsuario by remember { mutableStateOf("") }
    var edadUsuario by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var estratoUsuario by remember { mutableFloatStateOf(3f) }
    var direccionUsuario by remember { mutableStateOf("") }
    var correoUsuario by remember { mutableStateOf("") }
    var telefonoUsuario by remember { mutableStateOf("") }


    var nombreEmpresa by remember { mutableStateOf("") }
    var numeroContactoEmpresa by remember { mutableStateOf("") }
    var correoEmpresa by remember { mutableStateOf("") }
    var rutEmpresa by remember { mutableStateOf("") }
    var direccionEmpresa by remember { mutableStateOf("") }


    var contrasena by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val passEmptyErr = stringResource(R.string.err_password_empty)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.register_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackToLogin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.register_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = tipoUsuario == TipoUsuario.USUARIO,
                    onClick = { tipoUsuario = TipoUsuario.USUARIO },
                    label = { Text(stringResource(R.string.chip_user_tester)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.padding(end = 8.dp)
                )

                FilterChip(
                    selected = tipoUsuario == TipoUsuario.EMPRESA,
                    onClick = { tipoUsuario = TipoUsuario.EMPRESA },
                    label = { Text(stringResource(R.string.chip_empresa)) },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    if (tipoUsuario == TipoUsuario.USUARIO) {

                        OutlinedTextField(
                            value = nombreUsuario,
                            onValueChange = { nombreUsuario = it },
                            label = { Text(stringResource(R.string.field_full_name)) },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = edadUsuario,
                                onValueChange = { edadUsuario = it },
                                label = { Text(stringResource(R.string.field_age)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 6.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )

                            OutlinedTextField(
                                value = fechaNacimiento,
                                onValueChange = { fechaNacimiento = it },
                                label = { Text(stringResource(R.string.field_birth_date)) },
                                placeholder = { Text(stringResource(R.string.field_birth_date_placeholder)) },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .padding(start = 6.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))


                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(
                                text = stringResource(R.string.field_stratum, estratoUsuario.toInt()),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Slider(
                                value = estratoUsuario,
                                onValueChange = { estratoUsuario = it },
                                valueRange = 1f..6f,
                                steps = 4
                            )
                        }

                        OutlinedTextField(
                            value = direccionUsuario,
                            onValueChange = { direccionUsuario = it },
                            label = { Text(stringResource(R.string.field_address)) },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = correoUsuario,
                            onValueChange = { correoUsuario = it },
                            label = { Text(stringResource(R.string.field_email)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = telefonoUsuario,
                            onValueChange = { telefonoUsuario = it },
                            label = { Text(stringResource(R.string.field_phone)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                    } else {

                        OutlinedTextField(
                            value = nombreEmpresa,
                            onValueChange = { nombreEmpresa = it },
                            label = { Text(stringResource(R.string.field_company_name)) },
                            leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = rutEmpresa,
                            onValueChange = { rutEmpresa = it },
                            label = { Text(stringResource(R.string.field_company_rut)) },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = correoEmpresa,
                            onValueChange = { correoEmpresa = it },
                            label = { Text(stringResource(R.string.field_company_email)) },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = numeroContactoEmpresa,
                            onValueChange = { numeroContactoEmpresa = it },
                            label = { Text(stringResource(R.string.field_company_phone)) },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = direccionEmpresa,
                            onValueChange = { direccionEmpresa = it },
                            label = { Text(stringResource(R.string.field_company_address)) },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = contrasena,
                        onValueChange = { contrasena = it },
                        label = { Text(stringResource(R.string.field_password)) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    AnimatedVisibility(visible = errorMessage != null) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val correo = if (tipoUsuario == TipoUsuario.USUARIO) {
                                correoUsuario.trim()
                            } else {
                                correoEmpresa.trim()
                            }

                            val nombre = if (tipoUsuario == TipoUsuario.USUARIO) {
                                nombreUsuario.trim()
                            } else {
                                nombreEmpresa.trim()
                            }

                            // Validar campos obligatorios
                            if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                                errorMessage = "Completa el nombre, el correo y la contraseña."
                                return@Button
                            }

                            if (!android.util.Patterns.EMAIL_ADDRESS
                                    .matcher(correo).matches()) {
                                errorMessage = "Ingresa un correo electrónico válido."
                                return@Button
                            }

                            if (contrasena.length < 6) {
                                errorMessage = "La contraseña debe tener al menos 6 caracteres."
                                return@Button
                            }
                            if (tipoUsuario == TipoUsuario.USUARIO) {
                                if (edadUsuario.toIntOrNull() == null ||
                                    fechaNacimiento.isBlank() ||
                                    direccionUsuario.isBlank() ||
                                    telefonoUsuario.isBlank()
                                ) {
                                    errorMessage = "Completa todos los campos del usuario."
                                    return@Button
                                }
                            } else {
                                if (rutEmpresa.isBlank() ||
                                    numeroContactoEmpresa.isBlank() ||
                                    direccionEmpresa.isBlank()
                                ) {
                                    errorMessage = "Completa todos los campos de la empresa."
                                    return@Button
                                }
                            }

                            errorMessage = null

                            scope.launch {
                                isLoading = true

                                try {
                                    val resultado = if (tipoUsuario == TipoUsuario.USUARIO) {
                                        val usuario = Usuario(
                                            id = "",
                                            nombre = nombreUsuario.trim(),
                                            edad = edadUsuario.toInt(),
                                            fechaNacimiento = fechaNacimiento.trim(),
                                            estrato = estratoUsuario.toInt(),
                                            direccion = direccionUsuario.trim(),
                                            correo = correoUsuario.trim(),
                                            telefono = telefonoUsuario.trim()
                                        )

                                        authRepository.registrarUsuario(usuario, contrasena)
                                    } else {
                                        val empresa = Empresa(
                                            id = "",
                                            nombre = nombreEmpresa.trim(),
                                            numeroContacto = numeroContactoEmpresa.trim(),
                                            correo = correoEmpresa.trim(),
                                            rut = rutEmpresa.trim(),
                                            direccion = direccionEmpresa.trim()
                                        )

                                        authRepository.registrarEmpresa(empresa, contrasena)
                                    }

                                    resultado.onSuccess { estado ->
                                        if (estado.tipoUsuario == TipoUsuario.USUARIO) {
                                            onRegisterSuccessUsuario()
                                        } else {
                                            onRegisterSuccessEmpresa()
                                        }
                                    }.onFailure { error ->
                                        errorMessage = when {
                                            error is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                                                "Ya existe una cuenta con ese correo."

                                            error is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                                                "La contraseña no cumple los requisitos."

                                            else -> error.localizedMessage
                                                ?: "No se pudo completar el registro."
                                        }
                                    }
                                } catch (e: Exception) {
                                    errorMessage = e.localizedMessage
                                        ?: "Ocurrió un error durante el registro."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.btn_complete_registration),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

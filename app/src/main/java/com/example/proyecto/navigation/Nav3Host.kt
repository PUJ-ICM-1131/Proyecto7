package com.example.proyecto.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.proyecto.data.repository.AuthRepository
import com.example.proyecto.data.repository.FirebaseAuthRepository
import com.example.proyecto.data.repository.MockAuthRepository
import com.example.proyecto.data.repository.MockTestRepository
import com.example.proyecto.data.repository.TestRepository
import com.example.proyecto.ui.screens.EmpresaWelcomeScreen
import com.example.proyecto.ui.screens.LoginScreen
import com.example.proyecto.ui.screens.MapScreen
import com.example.proyecto.ui.screens.RegisterScreen
import com.example.proyecto.ui.screens.TestDetailScreen
import com.example.proyecto.ui.screens.UserMainScreen

@Composable
fun Nav3Host(
    navController: Nav3Controller = rememberNav3Controller(),
    authRepository: AuthRepository = remember { FirebaseAuthRepository() },
    testRepository: TestRepository = remember { MockTestRepository() }
) {
    val currentKey = navController.currentKey ?: Nav3Key.Login

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = currentKey,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "Nav3Transition"
        ) { key ->
            when (key) {
                is Nav3Key.Login -> {
                    LoginScreen(
                        authRepository = authRepository,
                        onNavigateToRegister = {
                            navController.navigateTo(Nav3Key.Register)
                        },
                        onLoginSuccessUsuario = {
                            navController.popAllAndNavigateTo(Nav3Key.UserMain)
                        },
                        onLoginSuccessEmpresa = {
                            navController.popAllAndNavigateTo(Nav3Key.EmpresaWelcome)
                        }
                    )
                }

                is Nav3Key.Register -> {
                    RegisterScreen(
                        authRepository = authRepository,
                        onBackToLogin = {
                            navController.popBackStack()
                        },
                        onRegisterSuccessUsuario = {
                            navController.popAllAndNavigateTo(Nav3Key.UserMain)
                        },
                        onRegisterSuccessEmpresa = {
                            navController.popAllAndNavigateTo(Nav3Key.EmpresaWelcome)
                        }
                    )
                }

                is Nav3Key.UserMain -> {
                    UserMainScreen(
                        authRepository = authRepository,
                        testRepository = testRepository,
                        onSelectTestDetail = { testId ->
                            navController.navigateTo(Nav3Key.TestDetail(testId))
                        },
                        onOpenFullMap = { testId ->
                            navController.navigateTo(Nav3Key.MapDetail(testId))
                        },
                        onLogout = {
                            navController.popAllAndNavigateTo(Nav3Key.Login)
                        }
                    )
                }

                is Nav3Key.EmpresaWelcome -> {
                    EmpresaWelcomeScreen(
                        authRepository = authRepository,
                        onLogout = {
                            navController.popAllAndNavigateTo(Nav3Key.Login)
                        }
                    )
                }

                is Nav3Key.TestDetail -> {
                    TestDetailScreen(
                        testId = key.testId,
                        testRepository = testRepository,
                        onBack = {
                            navController.popBackStack()
                        },
                        onInscripcionExitosa = {
                            navController.popBackStack()
                        }
                    )
                }
                else->{
                    LoginScreen(
                        authRepository = authRepository,
                        onNavigateToRegister = {
                            navController.navigateTo(Nav3Key.Register)
                        },
                        onLoginSuccessUsuario = {
                            navController.popAllAndNavigateTo(Nav3Key.UserMain)
                        },
                        onLoginSuccessEmpresa = {
                            navController.popAllAndNavigateTo(Nav3Key.EmpresaWelcome)
                        }
                    )
                }
            }
        }
    }
}

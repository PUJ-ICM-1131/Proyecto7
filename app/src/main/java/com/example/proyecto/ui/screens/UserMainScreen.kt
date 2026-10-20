package com.example.proyecto.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.proyecto.R
import com.example.proyecto.data.repository.AuthRepository
import com.example.proyecto.data.repository.TestRepository
import com.example.proyecto.ui.screens.tabs.EnCursoTab
import com.example.proyecto.ui.screens.tabs.ExplorarTab
import com.example.proyecto.ui.screens.tabs.HistorialTab
import com.example.proyecto.ui.screens.tabs.PerfilTab

sealed class BottomTab(val titleRes: Int, val icon: ImageVector) {
    data object Explorar : BottomTab(R.string.tab_explorar, Icons.Default.Explore)
    data object Historial : BottomTab(R.string.tab_historial, Icons.Default.History)
    data object EnCurso : BottomTab(R.string.tab_en_curso, Icons.AutoMirrored.Filled.DirectionsRun)
    data object Perfil : BottomTab(R.string.tab_perfil, Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMainScreen(
    authRepository: AuthRepository,
    testRepository: TestRepository,
    onSelectTestDetail: (String) -> Unit,
    onOpenFullMap: (String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTabItem by remember { mutableIntStateOf(0) }
    val tabs = listOf(BottomTab.Explorar, BottomTab.Historial, BottomTab.EnCurso, BottomTab.Perfil)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (selectedTabItem) {
                            0 -> stringResource(R.string.header_explorar)
                            1 -> stringResource(R.string.header_historial)
                            2 -> stringResource(R.string.header_en_curso)
                            else -> stringResource(R.string.header_perfil)
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    val tabLabel = stringResource(tab.titleRes)
                    NavigationBarItem(
                        selected = selectedTabItem == index,
                        onClick = { selectedTabItem = index },
                        icon = { Icon(tab.icon, contentDescription = tabLabel) },
                        label = { Text(tabLabel) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTabItem) {
                0 -> ExplorarTab(
                    testRepository = testRepository,
                    onTestClick = onSelectTestDetail
                )
                1 -> HistorialTab(
                    testRepository = testRepository
                )
                2 -> EnCursoTab(
                    testRepository = testRepository,
                    onOpenFullMap = onOpenFullMap
                )
                3 -> PerfilTab(
                    authRepository = authRepository,
                    onLogout = onLogout
                )
            }
        }
    }
}

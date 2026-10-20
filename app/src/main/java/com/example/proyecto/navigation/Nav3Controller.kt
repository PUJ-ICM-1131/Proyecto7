package com.example.proyecto.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList

class Nav3Controller(
    val backStack: SnapshotStateList<Nav3Key>
) {
    val currentKey: Nav3Key? get() = backStack.lastOrNull()

    fun navigateTo(key: Nav3Key) {
        backStack.add(key)
    }

    fun popBackStack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun replaceTop(key: Nav3Key) {
        if (backStack.isNotEmpty()) {
            backStack.removeAt(backStack.size - 1)
        }
        backStack.add(key)
    }

    fun popAllAndNavigateTo(key: Nav3Key) {
        backStack.clear()
        backStack.add(key)
    }
}

@Composable
fun rememberNav3Controller(initialKey: Nav3Key = Nav3Key.Login): Nav3Controller {
    val backStack = remember { mutableStateListOf<Nav3Key>(initialKey) }
    return remember { Nav3Controller(backStack) }
}

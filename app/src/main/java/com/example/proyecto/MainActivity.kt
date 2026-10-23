package com.example.proyecto

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.FragmentActivity
import com.example.proyecto.navigation.Nav3Host
import com.example.proyecto.ui.theme.ProyectoTheme
import org.osmdroid.config.Configuration

class MainActivity : FragmentActivity() {

    private var imagePickerCallback: ((Uri?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().apply {
            load(
                applicationContext,
                getSharedPreferences("osmdroid_map", MODE_PRIVATE)
            )
            userAgentValue = "ProyectoColombiaApp/1.0 (richinavas2007gmail.com)"
        }

        enableEdgeToEdge()
        setContent {
            ProyectoTheme {
                Nav3Host()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            imagePickerCallback?.invoke(data?.data)
        }
    }

    fun launchImagePicker(onUriSelected: (Uri?) -> Unit) {
        imagePickerCallback = onUriSelected
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, 1002)
    }
}

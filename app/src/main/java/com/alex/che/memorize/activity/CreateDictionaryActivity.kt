package com.alex.che.memorize.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import com.alex.che.memorize.ui.screens.CreateDictionaryScreen
import com.alex.che.memorize.ui.theme.MemorizeTheme

class CreateDictionaryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MemorizeTheme(
                darkTheme = isSystemInDarkTheme()
            ) {
                Surface {
                    CreateDictionaryScreen(
                        onNavigateBack = { finish() }
                    )
                }
            }
        }
    }
}
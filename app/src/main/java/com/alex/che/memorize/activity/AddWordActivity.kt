package com.alex.che.memorize.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alex.che.memorize.ui.screens.AddWordScreen
import com.alex.che.memorize.ui.theme.MemorizeTheme

class AddWordActivity : ComponentActivity() {

    private var dictionaryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)

        setContent {
            MemorizeTheme {
                AddWordScreen(
                    dictionaryId = dictionaryId,
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}
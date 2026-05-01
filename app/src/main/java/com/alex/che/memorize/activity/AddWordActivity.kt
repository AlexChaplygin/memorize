package com.alex.che.memorize.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import com.alex.che.memorize.ui.screens.AddWordScreen
import com.alex.che.memorize.ui.theme.MemorizeTheme
import com.alex.che.memorize.viewmodel.AddWordViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class AddWordActivity : ComponentActivity() {

    private var dictionaryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)

        setContent {
            MemorizeTheme(
                darkTheme = isSystemInDarkTheme()
            ) {
                Surface {
                    val viewModel: AddWordViewModel =
                        koinViewModel(parameters = { parametersOf(dictionaryId) })
                    AddWordScreen(
                        dictionaryId = dictionaryId,
                        viewModel = viewModel,
                        onNavigateBack = { 
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    )
                }
            }
        }
    }
}
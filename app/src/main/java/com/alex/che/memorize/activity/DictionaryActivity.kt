package com.alex.che.memorize.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import com.alex.che.memorize.MainActivity
import com.alex.che.memorize.domain.CsvService
import com.alex.che.memorize.repository.MemorizeDatabase
import com.alex.che.memorize.ui.screens.DictionaryScreen
import com.alex.che.memorize.ui.theme.MemorizeTheme
import com.alex.che.memorize.viewmodel.DictionaryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.system.exitProcess

class DictionaryActivity : ComponentActivity() {

    private val memorizeDatabase: MemorizeDatabase by inject()
    private val csvService: CsvService by inject()
    private var dictionaryId: Int = -1
    private var viewModel: DictionaryViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)

        if (dictionaryId == -1) {
            Toast.makeText(this, "Invalid dictionary ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            MemorizeTheme(
                darkTheme = isSystemInDarkTheme()
            ) {
                Surface {
                    viewModel = koinViewModel<DictionaryViewModel>(parameters = { parametersOf(dictionaryId) })
                    DictionaryScreen(
                        dictionaryId = dictionaryId,
                        viewModel = viewModel!!,
                        onNavigateBack = { backToMain() },
                        onAddWord = { addWord() },
                        onTrainWords = { trainWords() },
                        onTrainDifficultWords = { trainDifficultWords() },
                        onExportCsv = { exportDictionary() },
                        onImportCsv = { openDocumentPicker() },
                        onDeleteDictionary = { deleteDictionary() }
                    )
                }
            }
        }
    }

    private fun exit() {
        this.finish()
        exitProcess(0)
    }

    private fun exportDictionary() {
        lifecycleScope.launch {
            try {
                val dictionary = memorizeDatabase.dictionaryDao.findDictionaryById(dictionaryId)
                csvService.export(dictionaryId, dictionary.name)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DictionaryActivity, "Export finished.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DictionaryActivity, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteDictionary() {
        lifecycleScope.launch {
            try {
                memorizeDatabase.dictionaryDao.deleteDictionary(dictionaryId)
                val intent = Intent(this@DictionaryActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@DictionaryActivity, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addWord() {
        val intent = Intent(this, AddWordActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", dictionaryId)
        addWordLauncher.launch(intent)
    }

    private val addWordLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel?.refresh()
        }
    }

    private fun trainWords() {
        val intent = Intent(this, TrainWordsActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", dictionaryId)
        intent.putExtra("TRAIN_DIFFICULT_WORDS", false)
        startActivity(intent)
    }

    private fun trainDifficultWords() {
        val intent = Intent(this, TrainWordsActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", dictionaryId)
        intent.putExtra("TRAIN_DIFFICULT_WORDS", true)
        startActivity(intent)
    }

    private fun import() {
        openDocumentPicker()
    }

    val importFromCsvResult =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result ->
            if (result != null) {
                lifecycleScope.launch {
                    try {
                        csvService.import(result, dictionaryId, this@DictionaryActivity)
                        Toast.makeText(this@DictionaryActivity, "Import finished.", Toast.LENGTH_SHORT).show()
                        viewModel?.refresh()
                    } catch (e: Exception) {
                        Toast.makeText(this@DictionaryActivity, "Import failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun openDocumentPicker() {
        importFromCsvResult.launch("*/*")
    }

    private fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
package com.alex.che.memorize.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alex.che.memorize.MainActivity
import com.alex.che.memorize.R
import com.alex.che.memorize.domain.CsvService
import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess

class DictionaryActivity : AppCompatActivity() {

    private val memorizeDatabase: MemorizeDatabase by inject()
    private val csvService: CsvService by inject()
    private var dictionaryId: Int = -1
    private val TAG: String = "Dictionary"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dictionary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbar: Toolbar = findViewById(R.id.dictionary_toolbar)
        setSupportActionBar(toolbar)

        dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)

        val trainWordsBtn: Button = findViewById(R.id.train_words_btn)
        trainWordsBtn.setOnClickListener {
            trainWords()
        }

        val backToDictionariesBtn: ImageButton = findViewById(R.id.back_to_dictionaries_btn)!!
        backToDictionariesBtn.setOnClickListener {
            backToDictionaries()
        }

        val trainDiffWordsBtn: Button = findViewById(R.id.train_diff_words_btn)
        trainDiffWordsBtn.setOnClickListener {
            trainDifficultWords()
        }

        refreshWordsList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_new_word -> addWord()
            R.id.import_from_csv -> import()
            R.id.delete_dictionary -> deleteDictionary()
            R.id.export_to_csv -> exportDictionary()
            R.id.exit -> exit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.dictionary_menu, menu)
        return true
    }

    private fun exit() {
        this.finish()
        exitProcess(0)
    }

    private fun exportDictionary() {
        val dictionary = memorizeDatabase.dictionaryDao.findDictionaryById(dictionaryId)
        csvService.export(dictionaryId, dictionary.name)
        Toast.makeText(this, "Export finished.", Toast.LENGTH_SHORT).show()
    }

    private fun deleteDictionary() {
        memorizeDatabase.dictionaryDao.deleteDictionary(dictionaryId)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun addWord() {
        val intent = Intent(this, AddWordActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", dictionaryId)
        startActivity(intent)
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
        refreshWordsList()
    }

    private fun refreshWordsList() {
        val wordsCountTv: TextView = findViewById(R.id.words_count)
        val wordsInDictionary = memorizeDatabase.wordDao.loadWordsByDictId(dictionaryId)
        wordsCountTv.text = wordsInDictionary?.count().toString()
    }

    val importFromCsvResult =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result ->
            if (result != null) {
                this.csvService.import(result, dictionaryId, this)
                Toast.makeText(this, "Import finished.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun openDocumentPicker() {
        importFromCsvResult.launch("*/*")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "DictionaryActivity: onResume()")
        refreshWordsList()
    }

    private fun backToDictionaries() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
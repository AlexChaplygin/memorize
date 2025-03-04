package com.alex.che.memorize.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alex.che.memorize.R
import com.alex.che.memorize.entity.Word
import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.android.ext.android.inject
import java.time.LocalDateTime

class AddWordActivity : AppCompatActivity() {

    private val memorizeDatabase: MemorizeDatabase by inject()
    private var dictionaryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_word)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)

        val backBtn: Button = findViewById(R.id.back_from_new_word_btn)
        backBtn.setOnClickListener {
            backToDictionary()
        }

        val saveNewWordBtn: Button = findViewById(R.id.save_new_word_btn)
        saveNewWordBtn.setOnClickListener {
            val newWordEt: EditText = findViewById(R.id.new_word_et)
            val newWordTranslationEt: EditText = findViewById(R.id.new_word_translate_et)
            saveWord(newWordEt.text.trim().toString(), newWordTranslationEt.text.trim().toString())
            newWordEt.setText("")
            newWordTranslationEt.setText("")
        }
    }

    private fun backToDictionary() {
        val intent = Intent(this, DictionaryActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", dictionaryId)
        startActivity(intent)
        finish()
    }

    private fun saveWord(word: String, translation: String) {
        memorizeDatabase.wordDao.insertWord(
            Word(
                null,
                word,
                translation,
                true,
                dictionaryId,
                LocalDateTime.now()
            )
        )
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
    }
}
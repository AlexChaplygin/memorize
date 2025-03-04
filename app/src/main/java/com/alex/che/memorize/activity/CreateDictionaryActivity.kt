package com.alex.che.memorize.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alex.che.memorize.MainActivity
import com.alex.che.memorize.R
import com.alex.che.memorize.entity.Dictionary
import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.android.ext.android.inject
import java.time.LocalDateTime

class CreateDictionaryActivity : AppCompatActivity() {

    private val memorizeDatabase: MemorizeDatabase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_dictionary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn: Button = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            backToMain()
        }

        val saveDictionaryBtn: Button = findViewById(R.id.save_dictionary_btn)
        saveDictionaryBtn.setOnClickListener {
            val newDictionaryName: TextView = findViewById(R.id.new_dictionary_name)
            createDictionary(newDictionaryName.text.trim().toString())
        }
    }

    private fun createDictionary(name: String) {
        memorizeDatabase.dictionaryDao.insertDictionary(
            Dictionary(
                null,
                name,
                LocalDateTime.now()
            )
        )
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun backToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
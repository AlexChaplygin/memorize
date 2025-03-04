package com.alex.che.memorize

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alex.che.memorize.activity.CreateDictionaryActivity
import com.alex.che.memorize.activity.DictionaryActivity
import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.android.ext.android.inject
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val memorizeDatabase: MemorizeDatabase by inject()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val dictionariesHLinearLayoutOuter: LinearLayout = findViewById(R.id.dictionaries)
        dictionariesHLinearLayoutOuter.addView(getDictionariesView())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_dictionary -> createNewDictionary()
            R.id.exit -> exit()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDictionariesView(): LinearLayout {
        val dictionariesHLinearLayout = LinearLayout(this)
        dictionariesHLinearLayout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dictionariesHLinearLayout.gravity = Gravity.CENTER_VERTICAL
        dictionariesHLinearLayout.orientation = VERTICAL
        dictionariesHLinearLayout.background =
            ContextCompat.getDrawable(this, R.drawable.shape_rounded_conteiners)

        val dictionaries = memorizeDatabase.dictionaryDao.loadAllDictionaries()

        if (!dictionaries.isNullOrEmpty()) {
            dictionaries.forEach { dict ->
                val buttonView = Button(this)
                buttonView.text = dict.name
                buttonView.setTextColor(ColorStateList.valueOf(Color.WHITE))
                buttonView.width = LinearLayout.LayoutParams.MATCH_PARENT
                buttonView.height = 70
                buttonView.background =
                    ContextCompat.getDrawable(this, R.drawable.shape_rounded_variants)
                buttonView.setOnClickListener {
                    train(dict.id!!)
                }

                val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                params.setMargins(1, 5, 1, 1)
                buttonView.setLayoutParams(params)

                dictionariesHLinearLayout.addView(buttonView)
            }
        }
        return dictionariesHLinearLayout
    }

    private fun createNewDictionary() {
        val intent = Intent(this, CreateDictionaryActivity::class.java)
        startActivity(intent)
    }

    private fun train(id: Int) {
        val intent = Intent(this, DictionaryActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", id)
        startActivity(intent)
    }

    private fun exit() {
        this.finish()
        exitProcess(0)
    }
}
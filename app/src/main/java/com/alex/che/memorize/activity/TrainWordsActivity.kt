package com.alex.che.memorize.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alex.che.memorize.R
import com.alex.che.memorize.converter.WordConverter
import com.alex.che.memorize.dto.WordDto
import com.alex.che.memorize.fragment.CheckWordFragment
import com.alex.che.memorize.repository.MemorizeDatabase
import org.koin.android.ext.android.inject
import java.util.Stack
import java.util.stream.IntStream

class TrainWordsActivity : AppCompatActivity() {

    private val memorizeDatabase: MemorizeDatabase by inject()
    private val wordConverter: WordConverter by inject()
    private var dictionaryId: Int = -1
    private var wordsStack: Stack<WordDto> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_train_words)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)
        val trainDifficultWords = intent.getBooleanExtra("TRAIN_DIFFICULT_WORDS", false)

        val closeWordsTrainingBtn: ImageButton = findViewById(R.id.close_words_training_btn)
        closeWordsTrainingBtn.setOnClickListener {
            goToDictionary()
        }

        getWordsToTrain(trainDifficultWords)
        setNewFragment()
    }

    fun setNewFragment() {
        if (!wordsStack.empty()) {
            val word = wordsStack.pop()
            val bundle = Bundle()
            bundle.putSerializable("word", word)
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(
                R.id.layout_to_train_words_fragment,
                CheckWordFragment::class.java,
                bundle,
                "TAG"
            )
            ft.addToBackStack(null)
            ft.commit()
        } else {
            val intent = Intent(this, DictionaryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun goToDictionary() {
        val intent = Intent(this, DictionaryActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", dictionaryId)
        startActivity(intent)
        finish()
    }

    private fun getWordsToTrain(trainDifficultWords: Boolean) {
        Log.i("TrainWordsActivity", "getWordsToTrain()")
        val idsToTrain = mutableListOf<Int>()
        val wordsIdList =
            memorizeDatabase.wordDao.loadIdsByDictId(dictionaryId, trainDifficultWords)
        if (wordsIdList.isNullOrEmpty()) {
            Toast.makeText(this, "No words in dictionary.", Toast.LENGTH_SHORT).show()
            return
        }

        if (wordsIdList.size < 50) {
            val wordsList = memorizeDatabase.wordDao.loadWords(wordsIdList).shuffled()
            wordsList.map { wordConverter.convert(it) }
                .forEach { wordsStack.push(it) }
            return
        }

        IntStream.range(0, 50).forEach {
            idsToTrain.add(wordsIdList.random())
        }
        val wordsList = memorizeDatabase.wordDao.loadWords(idsToTrain).shuffled()
        wordsList.map { wordConverter.convert(it) }
            .forEach { wordsStack.push(it) }
        Log.i("TrainWordsActivity", "Got ${wordsStack.size} words.")
    }

}
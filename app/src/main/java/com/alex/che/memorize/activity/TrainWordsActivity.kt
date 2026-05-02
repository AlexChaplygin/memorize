package com.alex.che.memorize.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.alex.che.memorize.R
import com.alex.che.memorize.converter.WordConverter
import com.alex.che.memorize.dto.WordDto
import com.alex.che.memorize.fragment.CheckWordFragment
import com.alex.che.memorize.repository.MemorizeDatabase
import com.alex.che.memorize.ui.screens.TrainScreen
import com.alex.che.memorize.ui.theme.MemorizeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import java.util.Stack

private const val WORDS_TO_TRAIN = 50
private const val WORDS_TO_TAKE = 150

class TrainWordsActivity : AppCompatActivity() {

    private val memorizeDatabase: MemorizeDatabase by inject()
    private val wordConverter: WordConverter by inject()
    private var dictionaryId: Int = -1
    private var wordsAmount: Int = 0
    private var wordsStack: Stack<WordDto> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        wordsStack = Stack()

        dictionaryId = intent.getIntExtra("SELECTED_DICTIONARY_ID", -1)
        val trainDifficultWords = intent.getBooleanExtra("TRAIN_DIFFICULT_WORDS", false)

        setContent {
            MemorizeTheme {
                val gradientBrush = Brush.linearGradient(
                    0.0f to Color(0xFF2196F3),
                    1.0f to Color(0xFFF22828)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush)
                ) {
                    TrainScreen(
                        dictionaryId = dictionaryId,
                        trainDifficultWords = trainDifficultWords,
                        onNavigateBack = {
                            goToDictionary()
                        }
                    )
                }
            }
        }

        // Load words in background (kept for backward compatibility)
        CoroutineScope(Dispatchers.Main).launch {
            wordsAmount = getWordsToTrain(trainDifficultWords)
            Log.i("TrainWordsActivity", "Loaded $wordsAmount words for training")
        }
    }

    private fun goToDictionary() {
        val intent = Intent(this@TrainWordsActivity, DictionaryActivity::class.java)
        intent.putExtra("SELECTED_DICTIONARY_ID", dictionaryId)
        startActivity(intent)
        finish()
    }

    private suspend fun getWordsToTrain(trainDifficultWords: Boolean): Int = withContext(Dispatchers.IO) {
        Log.i("TrainWordsActivity", "getWordsToTrain()")
        val words = if (trainDifficultWords) {
            memorizeDatabase.wordDao.loadDifficultWordsByDictId(dictionaryId)
        } else {
            memorizeDatabase.wordDao.loadByDictId(dictionaryId)
        }
        if (words.isEmpty()) {
            runOnUiThread {
                Toast.makeText(this@TrainWordsActivity, "No words in dictionary.", Toast.LENGTH_SHORT).show()
            }
            return@withContext 0
        }

        val takenList = words.take(WORDS_TO_TAKE).shuffled().take(WORDS_TO_TRAIN)
        takenList.map { wordConverter.convert(it) }
            .forEach { wordsStack.push(it) }

        Log.i("TrainWordsActivity", "Got ${wordsStack.size} words.")
        return@withContext takenList.size
    }

    // Kept for backward compatibility with CheckWordFragment (old XML-based flow)
    // Not used in Compose-based flow
    @Deprecated("Old Fragment-based flow, use Compose TrainScreen instead")
    fun setNewFragment() {
        if (!wordsStack.empty()) {
            val word = wordsStack.pop()
            val bundle = Bundle()
            bundle.putSerializable("word", word)
            bundle.putInt("current_count", wordsAmount - wordsStack.size)
            bundle.putInt("amount_of_words", wordsAmount)
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
            goToDictionary()
        }
    }
}
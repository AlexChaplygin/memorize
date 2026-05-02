package com.alex.che.memorize.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.che.memorize.entity.Word
import com.alex.che.memorize.repository.MemorizeDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.time.LocalDateTime
import java.util.Collections
import java.util.Stack

private const val WORDS_TO_TRAIN = 50
private const val WORDS_TO_TAKE = 150

@Factory
class TrainViewModel(
    private val memorizeDatabase: MemorizeDatabase,
    private val dictionaryId: Int,
    private val trainDifficultWords: Boolean
) : ViewModel() {

    private val _state = MutableStateFlow(TrainState())
    val state: StateFlow<TrainState> = _state.asStateFlow()

    private var wordsStack: Stack<Word> = Stack()
    private var totalWordsCount: Int = 0

    init {
        loadWords()
    }

    private fun loadWords() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val words = if (trainDifficultWords) {
                    memorizeDatabase.wordDao.loadDifficultWordsByDictId(dictionaryId)
                } else {
                    memorizeDatabase.wordDao.loadByDictId(dictionaryId)
                }

                if (words.isEmpty()) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No words to train"
                    )
                    return@launch
                }

                val takenList = words.take(WORDS_TO_TAKE).shuffled().take(WORDS_TO_TRAIN)
                totalWordsCount = takenList.size
                val reversedList = takenList.reversed()
                wordsStack.addAll(reversedList) // Reverse so pop() gives first word

                if (wordsStack.isNotEmpty()) {
                    loadNextWord()
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "No words to train"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    private fun loadNextWord() {
        if (wordsStack.isNotEmpty()) {
            val word = wordsStack.pop()
            val currentCount = totalWordsCount - wordsStack.size
            _state.value = _state.value.copy(
                currentWord = word,
                userAnswer = "",
                isAnswerCorrect = null,
                currentCount = currentCount,
                totalWords = totalWordsCount,
                isLoading = false
            )
        } else {
            _state.value = _state.value.copy(
                isLoading = false,
                trainingComplete = true
            )
        }
    }

    fun onUserAnswerChange(answer: String) {
        _state.value = _state.value.copy(userAnswer = answer)
    }

    fun checkAnswer() {
        viewModelScope.launch {
            val currentWord = _state.value.currentWord ?: return@launch
            val userAnswer = _state.value.userAnswer

            val isCorrect = userAnswer.trim() == currentWord.word.trim()

            if (isCorrect) {
                memorizeDatabase.wordDao.changeWordCheckDateById(
                    currentWord.id,
                    LocalDateTime.now()
                )
                
                _state.value = _state.value.copy(
                    isAnswerCorrect = isCorrect,
                    showResult = true
                )

                // Auto-advance after delay only for correct answer
                kotlinx.coroutines.delay(1000)
                loadNextWord()
                _state.value = _state.value.copy(showResult = false)
            } else {
                // Show error but don't advance
                _state.value = _state.value.copy(
                    isAnswerCorrect = isCorrect,
                    showResult = true
                )
            }
        }
    }

    fun toggleIsDifficult(isDifficult: Boolean) {
        viewModelScope.launch {
            val currentWord = _state.value.currentWord ?: return@launch
            memorizeDatabase.wordDao.changeIsDifficult(currentWord.id, isDifficult)
        }
    }

    fun addHintChar(char: Char) {
        _state.value = _state.value.copy(
            userAnswer = _state.value.userAnswer + char
        )
    }

    fun removeHintChar() {
        val currentAnswer = _state.value.userAnswer
        if (currentAnswer.isNotEmpty()) {
            _state.value = _state.value.copy(
                userAnswer = currentAnswer.dropLast(1)
            )
        }
    }

    fun addHintFromHelp() {
        val currentWord = _state.value.currentWord ?: return
        val currentAnswer = _state.value.userAnswer
        val targetWord = currentWord.word
        
        // Find the first position where currentAnswer differs from targetWord
        for (i in 0 until targetWord.length) {
            if (i >= currentAnswer.length || currentAnswer[i] != targetWord[i]) {
                // Set text up to this position (inclusive) with correct letter
                _state.value = _state.value.copy(
                    userAnswer = targetWord.substring(0, i + 1)
                )
                return
            }
        }
    }

    fun getHintChar(index: Int): Char? {
        val currentWord = _state.value.currentWord ?: return null
        val wordCharArray = currentWord.word.toCharArray().distinct().toCharArray()
        wordCharArray.shuffle()
        return if (index < wordCharArray.size) wordCharArray[index] else null
    }

    fun getHintChars(): List<Char> {
        val currentWord = _state.value.currentWord ?: return emptyList()
        val wordCharArray = currentWord.word.toCharArray().distinct().toCharArray()
        return wordCharArray.toList().shuffled()
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onNavigateBack() {
        _state.value = _state.value.copy(navigateBack = true)
    }
}

data class TrainState(
    val currentWord: Word? = null,
    val userAnswer: String = "",
    val isAnswerCorrect: Boolean? = null,
    val currentCount: Int = 0,
    val totalWords: Int = 0,
    val isLoading: Boolean = false,
    val trainingComplete: Boolean = false,
    val error: String? = null,
    val showResult: Boolean = false,
    val navigateBack: Boolean = false
)

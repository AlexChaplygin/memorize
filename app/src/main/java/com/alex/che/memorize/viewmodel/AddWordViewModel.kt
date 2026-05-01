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

@Factory
class AddWordViewModel(
    private val memorizeDatabase: MemorizeDatabase,
    private val dictionaryId: Int
) : ViewModel() {

    private val stateFlow = MutableStateFlow(AddWordState())
    val state: StateFlow<AddWordState> = stateFlow.asStateFlow()

    fun saveWord(word: String, translation: String) {
        viewModelScope.launch {
            stateFlow.value = stateFlow.value.copy(isLoading = true, error = null)

            try {
                val normalizedTranslation = translation.replace("\\r?\\n".toRegex(), " ")
                memorizeDatabase.wordDao.insertWord(
                    Word(
                        id = null,
                        word = word,
                        translation = normalizedTranslation,
                        isDifficult = true,
                        dictionaryId = dictionaryId,
                        createdDate = LocalDateTime.now(),
                        checkDate = LocalDateTime.of(1999, 1, 1, 1, 1)
                    )
                )
                stateFlow.value = stateFlow.value.copy(
                    isLoading = false,
                    word = "",
                    translation = "",
                    successMessage = "Added"
                )
            } catch (e: Exception) {
                stateFlow.value = stateFlow.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    fun clearSuccessMessage() {
        stateFlow.value = stateFlow.value.copy(successMessage = null)
    }

    fun onWordChange(word: String) {
        stateFlow.value = stateFlow.value.copy(word = word)
    }

    fun onTranslationChange(translation: String) {
        stateFlow.value = stateFlow.value.copy(translation = translation)
    }
}

data class AddWordState(
    val word: String = "",
    val translation: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

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

@Factory
class DictionaryViewModel(
    private val database: MemorizeDatabase,
    private val dictionaryId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(DictionaryState())
    val state: StateFlow<DictionaryState> = _state.asStateFlow()

    init {
        loadWords()
    }

    fun loadWords() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val words = database.wordDao.loadByDictId(dictionaryId)
                val dictionary = database.dictionaryDao.findDictionaryById(dictionaryId)
                _state.value = _state.value.copy(
                    words = words,
                    dictionaryName = dictionary?.name ?: "",
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to load words: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun refresh() {
        loadWords()
    }

    override fun onCleared() {
        super.onCleared()
        // Очистка ресурсов при уничтожении ViewModel
    }
}

data class DictionaryState(
    val words: List<Word> = emptyList(),
    val dictionaryName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

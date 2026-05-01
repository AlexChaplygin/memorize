package com.alex.che.memorize.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.che.memorize.entity.Dictionary
import com.alex.che.memorize.repository.MemorizeDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class CreateDictionaryViewModel(
    private val database: MemorizeDatabase
) : ViewModel() {

    private val stateFlow = MutableStateFlow(CreateDictionaryState())
    val state: StateFlow<CreateDictionaryState> = stateFlow.asStateFlow()

    fun onDictionaryNameChange(name: String) {
        stateFlow.value = stateFlow.value.copy(dictionaryName = name)
    }

    fun createDictionary() {
        viewModelScope.launch {
            val name = stateFlow.value.dictionaryName.trim()
            if (name.isBlank()) {
                stateFlow.value = stateFlow.value.copy(error = "Dictionary name cannot be empty")
                return@launch
            }

            stateFlow.value = stateFlow.value.copy(isLoading = true, error = null)

            try {
                database.dictionaryDao.insertDictionary(
                    Dictionary(
                        null,
                        name,
                        LocalDateTime.now()
                    )
                )
                stateFlow.value = stateFlow.value.copy(isLoading = false, success = true)
            } catch (e: Exception) {
                stateFlow.value = stateFlow.value.copy(
                    isLoading = false,
                    error = "Failed to create dictionary: ${e.message}"
                )
            }
        }
    }

    fun clearSuccess() {
        stateFlow.value = stateFlow.value.copy(success = false)
    }

    fun clearError() {
        stateFlow.value = stateFlow.value.copy(error = null)
    }
}

data class CreateDictionaryState(
    val dictionaryName: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

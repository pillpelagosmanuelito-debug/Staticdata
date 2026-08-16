package com.educalab.staticdata.ui.screens.academy

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AcademyUiState(
    val index: Int = 0,
    val correctCount: Int = 0,
    val lastAnswerCorrect: Boolean? = null,
    val finished: Boolean = false
)

class AcademyViewModel : ViewModel() {
    private val _state = MutableStateFlow(AcademyUiState())
    val state: StateFlow<AcademyUiState> = _state
    val items = AcademyContent.items

    fun answer(isCategorical: Boolean) {
        val current = _state.value
        val item = items[current.index]
        val correct = item.isCategorical == isCategorical
        _state.value = current.copy(lastAnswerCorrect = correct, correctCount = current.correctCount + if (correct) 1 else 0)
    }

    fun next() {
        val current = _state.value
        val nextIndex = current.index + 1
        _state.value = if (nextIndex >= items.size) current.copy(finished = true, lastAnswerCorrect = null)
        else current.copy(index = nextIndex, lastAnswerCorrect = null)
    }

    fun restart() { _state.value = AcademyUiState() }
}

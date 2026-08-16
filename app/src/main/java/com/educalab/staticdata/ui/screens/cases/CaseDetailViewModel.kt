package com.educalab.staticdata.ui.screens.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.data.repository.ExerciseRepository
import com.educalab.staticdata.domain.model.CaseFile
import com.educalab.staticdata.domain.model.Exercise
import com.educalab.staticdata.domain.usecase.SubmitAnswerResult
import com.educalab.staticdata.domain.usecase.SubmitExerciseAnswerUseCase
import com.educalab.staticdata.util.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CaseDetailUiState(
    val case: CaseFile? = null,
    val exercises: List<Exercise> = emptyList(),
    val currentIndex: Int = 0,
    val lastResult: SubmitAnswerResult? = null,
    val finished: Boolean = false,
    val loading: Boolean = true
)

class CaseDetailViewModel(
    private val caseId: Long,
    private val caseRepository: CaseRepository,
    private val exerciseRepository: ExerciseRepository,
    private val submitExerciseAnswerUseCase: SubmitExerciseAnswerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CaseDetailUiState())
    val state: StateFlow<CaseDetailUiState> = _state

    init {
        viewModelScope.launch {
            val case = caseRepository.getCase(caseId)
            val exercises = exerciseRepository.getForCase(caseId)
            _state.value = CaseDetailUiState(case = case, exercises = exercises, loading = false)
        }
    }

    fun submitAnswer(given: List<String>) {
        val exercise = _state.value.exercises.getOrNull(_state.value.currentIndex) ?: return
        viewModelScope.launch {
            val result = submitExerciseAnswerUseCase(CurrentUser.id, exercise, given)
            _state.value = _state.value.copy(lastResult = result)
        }
    }

    fun advance() {
        val current = _state.value
        val nextIndex = current.currentIndex + 1
        if (nextIndex >= current.exercises.size) {
            _state.value = current.copy(finished = true, lastResult = null)
        } else {
            _state.value = current.copy(currentIndex = nextIndex, lastResult = null)
        }
    }

    fun clearFeedback() {
        _state.value = _state.value.copy(lastResult = null)
    }
}

package com.educalab.staticdata.ui.screens.classify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.Dataset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class ClassifyUiState(
    val availableDatasets: List<Dataset> = emptyList(),
    val activeDataset: Dataset? = null,
    val bins: List<String> = emptyList(),
    val pending: List<DataValue> = emptyList(),
    val placedCorrectly: Map<String, Int> = emptyMap(),
    val mistakes: Int = 0,
    val roundComplete: Boolean = false
)

/**
 * Módulo "Organizador y clasificación": el niño arrastra/toca cada dato y
 * lo coloca en el contenedor (bin) correcto. Es una mecánica de
 * clasificación real, no un cuestionario de opción múltiple.
 */
class ClassifyViewModel(private val caseRepository: CaseRepository) : ViewModel() {

    private val _state = MutableStateFlow(ClassifyUiState())
    val state: StateFlow<ClassifyUiState> = _state

    init {
        viewModelScope.launch {
            caseRepository.observeDatasets().collect { list ->
                val categorical = list.filter { !it.isUserGenerated }
                _state.value = _state.value.copy(availableDatasets = categorical)
                if (_state.value.activeDataset == null && categorical.isNotEmpty()) {
                    loadRandomDataset(categorical)
                }
            }
        }
    }

    fun startNewRound() {
        val list = _state.value.availableDatasets
        if (list.isNotEmpty()) viewModelScope.launch { loadRandomDataset(list) }
    }

    private suspend fun loadRandomDataset(list: List<Dataset>) {
        val chosen = list.random(Random(System.nanoTime()))
        val full = caseRepository.getDatasetWithValues(chosen.id) ?: return
        val subset = full.values.shuffled().take(12)
        val bins = subset.map { it.label }.distinct().shuffled()
        _state.value = ClassifyUiState(
            availableDatasets = list,
            activeDataset = full,
            bins = bins,
            pending = subset,
            placedCorrectly = bins.associateWith { 0 },
            mistakes = 0,
            roundComplete = false
        )
    }

    fun placeInBin(value: DataValue, bin: String) {
        val current = _state.value
        val remaining = current.pending.filterNot { it.id == value.id }
        val correct = value.label == bin
        val updatedCounts = if (correct) {
            current.placedCorrectly.toMutableMap().apply { this[bin] = (this[bin] ?: 0) + 1 }
        } else current.placedCorrectly
        _state.value = current.copy(
            pending = remaining,
            placedCorrectly = updatedCounts,
            mistakes = current.mistakes + if (correct) 0 else 1,
            roundComplete = remaining.isEmpty()
        )
    }
}

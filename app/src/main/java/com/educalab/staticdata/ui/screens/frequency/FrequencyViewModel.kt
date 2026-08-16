package com.educalab.staticdata.ui.screens.frequency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.data.repository.FrequencyRepository
import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.Dataset
import com.educalab.staticdata.domain.model.FrequencyTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FrequencyUiState(
    val datasets: List<Dataset> = emptyList(),
    val activeDataset: Dataset? = null,
    val values: List<DataValue> = emptyList(),
    val labels: List<String> = emptyList(),
    val userCounts: Map<String, String> = emptyMap(),
    val revealed: FrequencyTable? = null
)

class FrequencyViewModel(
    private val caseRepository: CaseRepository,
    private val frequencyRepository: FrequencyRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FrequencyUiState())
    val state: StateFlow<FrequencyUiState> = _state

    init {
        viewModelScope.launch {
            caseRepository.observeDatasets().collect { list ->
                val categorical = list.filter { !it.isUserGenerated }
                _state.value = _state.value.copy(datasets = categorical)
                if (_state.value.activeDataset == null && categorical.isNotEmpty()) selectDataset(categorical.first().id)
            }
        }
    }

    fun selectDataset(datasetId: Long) {
        viewModelScope.launch {
            val full = caseRepository.getDatasetWithValues(datasetId) ?: return@launch
            val labels = full.values.map { it.label }.distinct().sorted()
            _state.value = _state.value.copy(
                activeDataset = full, values = full.values, labels = labels,
                userCounts = labels.associateWith { "" }, revealed = null
            )
        }
    }

    fun updateCount(label: String, value: String) {
        if (value.isNotEmpty() && value.toIntOrNull() == null) return
        _state.value = _state.value.copy(userCounts = _state.value.userCounts.toMutableMap().apply { this[label] = value })
    }

    fun checkTable() {
        val current = _state.value
        val real = StatsEngine.frequencyTable(current.values)
        viewModelScope.launch {
            current.activeDataset?.let { frequencyRepository.computeAndCache("DATASET", it.id, current.values) }
        }
        _state.value = current.copy(revealed = real)
    }

    fun isRowCorrect(label: String): Boolean? {
        val revealed = _state.value.revealed ?: return null
        val real = revealed.rows.firstOrNull { it.label == label }?.count ?: 0
        val given = _state.value.userCounts[label]?.toIntOrNull() ?: -1
        return given == real
    }
}

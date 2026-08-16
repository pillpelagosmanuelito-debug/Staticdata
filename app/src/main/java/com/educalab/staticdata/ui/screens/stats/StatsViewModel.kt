package com.educalab.staticdata.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.DataValue
import com.educalab.staticdata.domain.model.Dataset
import com.educalab.staticdata.domain.model.FrequencyTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StatsUiState(
    val datasets: List<Dataset> = emptyList(),
    val activeDataset: Dataset? = null,
    val values: List<DataValue> = emptyList(),
    val table: FrequencyTable? = null,
    val selectedLabel: String? = null,
    val predictedPercentage: Float = 50f,
    val revealedPercentage: Double? = null
)

class StatsViewModel(private val caseRepository: CaseRepository) : ViewModel() {

    private val _state = MutableStateFlow(StatsUiState())
    val state: StateFlow<StatsUiState> = _state

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
            val table = StatsEngine.frequencyTable(full.values)
            _state.value = StatsUiState(
                datasets = _state.value.datasets, activeDataset = full, values = full.values, table = table,
                selectedLabel = table.rows.firstOrNull()?.label, predictedPercentage = 50f, revealedPercentage = null
            )
        }
    }

    fun selectLabel(label: String) {
        _state.value = _state.value.copy(selectedLabel = label, predictedPercentage = 50f, revealedPercentage = null)
    }

    fun updatePrediction(value: Float) {
        _state.value = _state.value.copy(predictedPercentage = value)
    }

    fun reveal() {
        val current = _state.value
        val row = current.table?.rows?.firstOrNull { it.label == current.selectedLabel } ?: return
        val real = StatsEngine.percentage(row.count, current.table.total)
        _state.value = current.copy(revealedPercentage = real)
    }
}

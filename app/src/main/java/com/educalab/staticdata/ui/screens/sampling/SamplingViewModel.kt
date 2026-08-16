package com.educalab.staticdata.ui.screens.sampling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.SampleLabRepository
import com.educalab.staticdata.domain.model.SampleExperiment
import com.educalab.staticdata.domain.model.SampleRun
import com.educalab.staticdata.domain.model.SampleVariabilityResult
import com.educalab.staticdata.domain.usecase.RunSampleExperimentUseCase
import com.educalab.staticdata.util.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SamplingUiState(
    val experiments: List<SampleExperiment> = emptyList(),
    val activeExperiment: SampleExperiment? = null,
    val sampleSize: Int = 8,
    val runs: List<SampleRun> = emptyList(),
    val variability: SampleVariabilityResult? = null
)

class SamplingViewModel(
    private val sampleLabRepository: SampleLabRepository,
    private val runSampleExperimentUseCase: RunSampleExperimentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SamplingUiState())
    val state: StateFlow<SamplingUiState> = _state

    init {
        viewModelScope.launch {
            sampleLabRepository.observeExperiments().collect { list ->
                _state.value = _state.value.copy(experiments = list)
                if (_state.value.activeExperiment == null && list.isNotEmpty()) selectExperiment(list.first().id)
            }
        }
    }

    private var runsCollectionJob: kotlinx.coroutines.Job? = null

    fun selectExperiment(experimentId: Long) {
        val experiment = _state.value.experiments.firstOrNull { it.id == experimentId } ?: return
        _state.value = _state.value.copy(activeExperiment = experiment, runs = emptyList(), variability = null)
        runsCollectionJob?.cancel()
        runsCollectionJob = viewModelScope.launch {
            sampleLabRepository.observeRuns(experimentId).collect { runs ->
                _state.value = _state.value.copy(runs = runs)
                if (runs.isNotEmpty()) {
                    _state.value = _state.value.copy(variability = sampleLabRepository.variabilityFor(experimentId, runs))
                }
            }
        }
    }

    fun setSampleSize(size: Int) {
        _state.value = _state.value.copy(sampleSize = size)
    }

    fun drawSample() {
        val experiment = _state.value.activeExperiment ?: return
        viewModelScope.launch {
            runSampleExperimentUseCase(CurrentUser.id, experiment.id, experiment.populationDatasetId, _state.value.sampleSize)
        }
    }
}

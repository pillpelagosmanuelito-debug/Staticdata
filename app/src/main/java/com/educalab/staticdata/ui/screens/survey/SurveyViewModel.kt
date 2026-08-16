package com.educalab.staticdata.ui.screens.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.FrequencyRepository
import com.educalab.staticdata.data.repository.SurveyRepository
import com.educalab.staticdata.domain.logic.StatsEngine
import com.educalab.staticdata.domain.model.FrequencyTable
import com.educalab.staticdata.domain.model.Survey
import com.educalab.staticdata.domain.model.SurveyOption
import com.educalab.staticdata.domain.usecase.CreateSurveyUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SurveyUiState(
    val surveys: List<Survey> = emptyList(),
    val selectedSurveyId: Long? = null,
    val options: List<SurveyOption> = emptyList(),
    val table: FrequencyTable? = null,
    val errorMessage: String? = null
)

class SurveyViewModel(
    private val surveyRepository: SurveyRepository,
    private val frequencyRepository: FrequencyRepository,
    private val createSurveyUseCase: CreateSurveyUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SurveyUiState())
    val state: StateFlow<SurveyUiState> = _state

    init {
        viewModelScope.launch {
            surveyRepository.observeSurveys().collect { list -> _state.value = _state.value.copy(surveys = list) }
        }
    }

    fun createSurvey(question: String, options: List<String>) {
        viewModelScope.launch {
            try {
                val id = createSurveyUseCase(question, options)
                _state.value = _state.value.copy(errorMessage = null)
                selectSurvey(id)
            } catch (e: StatsEngine.InvalidDataException) {
                _state.value = _state.value.copy(errorMessage = e.message)
            }
        }
    }

    fun selectSurvey(surveyId: Long) {
        viewModelScope.launch {
            val options = surveyRepository.getOptions(surveyId)
            _state.value = _state.value.copy(selectedSurveyId = surveyId, options = options, table = null)
        }
    }

    fun respond(optionId: Long, alias: String) {
        val surveyId = _state.value.selectedSurveyId ?: return
        viewModelScope.launch {
            surveyRepository.recordResponse(surveyId, optionId, alias)
            refreshTable(surveyId)
        }
    }

    fun refreshTable(surveyId: Long) {
        viewModelScope.launch {
            val values = surveyRepository.getSurveyAsDataValues(surveyId)
            val table = frequencyRepository.computeAndCache("SURVEY", surveyId, values)
            _state.value = _state.value.copy(table = table)
        }
    }

    fun clearError() { _state.value = _state.value.copy(errorMessage = null) }
}

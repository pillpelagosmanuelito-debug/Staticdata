package com.educalab.staticdata.ui.screens.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.domain.model.CaseFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CasesViewModel(private val caseRepository: CaseRepository) : ViewModel() {
    private val _cases = MutableStateFlow<List<CaseFile>>(emptyList())
    val cases: StateFlow<List<CaseFile>> = _cases

    init {
        viewModelScope.launch {
            caseRepository.observeCases().collect { _cases.value = it.sortedBy { c -> c.order } }
        }
    }
}

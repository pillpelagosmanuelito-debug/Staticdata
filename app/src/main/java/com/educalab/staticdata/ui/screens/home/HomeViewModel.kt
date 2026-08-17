package com.educalab.staticdata.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.data.repository.ProfileRepository
import com.educalab.staticdata.domain.logic.ProgressRules
import com.educalab.staticdata.domain.model.CaseFile
import com.educalab.staticdata.domain.model.CaseStatus
import com.educalab.staticdata.domain.model.Progress
import com.educalab.staticdata.domain.model.UserProfile
import com.educalab.staticdata.util.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val profile: UserProfile? = null,
    val progress: Progress = Progress(0, 0, 1, 0, 0, 0),
    val nextCase: CaseFile? = null,
    val casesAvailable: Int = 0,
    val casesTotal: Int = 0
)

class HomeViewModel(
    private val profileRepository: ProfileRepository,
    private val caseRepository: CaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state

    init {
        viewModelScope.launch {
            combine(
                profileRepository.observeProfile(CurrentUser.id),
                profileRepository.observeProgress(CurrentUser.id),
                caseRepository.observeCases()
            ) { profile, progress, cases ->
                val nextCase = cases.firstOrNull { it.status == CaseStatus.DISPONIBLE || it.status == CaseStatus.INICIADO }
                    ?: cases.firstOrNull { it.status == CaseStatus.BLOQUEADO }
                HomeUiState(
                    profile = profile,
                    progress = progress ?: Progress(CurrentUser.id, 0, 1, 0, 0, 0),
                    nextCase = nextCase,
                    casesAvailable = cases.count { it.status != CaseStatus.BLOQUEADO },
                    casesTotal = cases.size
                )
            }.collect { _state.value = it }
        }
    }

    fun xpToNextLevel(): Int? = ProgressRules.xpForNextLevel(_state.value.progress.totalXp)
}

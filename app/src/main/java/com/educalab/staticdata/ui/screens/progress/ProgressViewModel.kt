package com.educalab.staticdata.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.CaseRepository
import com.educalab.staticdata.data.repository.ProfileRepository
import com.educalab.staticdata.domain.model.Badge
import com.educalab.staticdata.domain.model.Progress
import com.educalab.staticdata.util.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ProgressUiState(
    val progress: Progress = Progress(0, 0, 1, 0, 0, 0),
    val badges: List<Badge> = emptyList(),
    val unlockedIds: Set<Long> = emptySet(),
    val totalCases: Int = 0
)

class ProgressViewModel(
    private val profileRepository: ProfileRepository,
    private val caseRepository: CaseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProgressUiState())
    val state: StateFlow<ProgressUiState> = _state

    init {
        viewModelScope.launch {
            combine(
                profileRepository.observeProgress(CurrentUser.id),
                profileRepository.observeAllBadges(),
                profileRepository.observeUnlockedBadges(CurrentUser.id)
            ) { progress, badges, unlocked ->
                ProgressUiState(
                    progress = progress ?: Progress(CurrentUser.id, 0, 1, 0, 0, 0),
                    badges = badges,
                    unlockedIds = unlocked.map { it.badgeId }.toSet(),
                    totalCases = caseRepository.totalCases()
                )
            }.collect { _state.value = it }
        }
    }
}

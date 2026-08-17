package com.educalab.staticdata.ui.screens.profileselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.ProfileRepository
import com.educalab.staticdata.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileSelectUiState(
    val profiles: List<UserProfile> = emptyList(),
    val loading: Boolean = true
)

class ProfileSelectViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    private val _state = MutableStateFlow(ProfileSelectUiState())
    val state: StateFlow<ProfileSelectUiState> = _state

    init {
        viewModelScope.launch {
            profileRepository.observeAllProfiles().collect { profiles ->
                _state.value = ProfileSelectUiState(profiles = profiles, loading = false)
            }
        }
    }
}

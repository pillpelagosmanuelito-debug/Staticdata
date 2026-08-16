package com.educalab.staticdata.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.ProfileRepository
import com.educalab.staticdata.domain.model.UserProfile
import com.educalab.staticdata.util.CurrentUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    init {
        viewModelScope.launch { profileRepository.observeProfile().collect { _profile.value = it } }
    }

    fun updateAlias(alias: String, avatarId: Int) {
        viewModelScope.launch { profileRepository.updateAlias(CurrentUser.id, alias, avatarId) }
    }

    fun setSoundEnabled(enabled: Boolean) { viewModelScope.launch { profileRepository.setSoundEnabled(enabled) } }
    fun setHapticsEnabled(enabled: Boolean) { viewModelScope.launch { profileRepository.setHapticsEnabled(enabled) } }
}

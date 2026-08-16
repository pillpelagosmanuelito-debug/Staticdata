package com.educalab.staticdata.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.ProfileRepository
import com.educalab.staticdata.util.CurrentUser
import kotlinx.coroutines.launch

class OnboardingViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    fun finish(alias: String, avatarId: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            val finalAlias = alias.trim().ifBlank { "Detective" }
            profileRepository.updateAlias(CurrentUser.id, finalAlias, avatarId)
            profileRepository.completeOnboarding(CurrentUser.id)
            onDone()
        }
    }
}

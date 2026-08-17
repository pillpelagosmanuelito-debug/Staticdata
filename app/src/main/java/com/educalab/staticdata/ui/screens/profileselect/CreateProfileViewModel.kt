package com.educalab.staticdata.ui.screens.profileselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.staticdata.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class CreateProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    fun create(alias: String, avatarId: Int, onDone: (profileId: Long) -> Unit) {
        viewModelScope.launch {
            val finalAlias = alias.trim().ifBlank { "Detective" }
            val profile = profileRepository.createProfile(finalAlias, avatarId)
            onDone(profile.id)
        }
    }
}

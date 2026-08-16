package com.educalab.staticdata.data.repository

import com.educalab.staticdata.data.local.dao.BadgeDao
import com.educalab.staticdata.data.local.dao.ProgressDao
import com.educalab.staticdata.data.local.dao.UserProfileDao
import com.educalab.staticdata.data.local.entity.ProgressEntity
import com.educalab.staticdata.data.local.entity.UserBadgeEntity
import com.educalab.staticdata.data.local.entity.UserProfileEntity
import com.educalab.staticdata.domain.model.Badge
import com.educalab.staticdata.domain.model.Progress
import com.educalab.staticdata.domain.model.UserBadge
import com.educalab.staticdata.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileRepository(
    private val profileDao: UserProfileDao,
    private val progressDao: ProgressDao,
    private val badgeDao: BadgeDao
) {
    fun observeProfile(): Flow<UserProfile?> = profileDao.observeProfile().map { it?.toDomain() }

    suspend fun getOrCreateProfile(defaultAlias: String, defaultAvatarId: Int): UserProfile {
        profileDao.getProfileOnce()?.let { return it.toDomain() }
        val entity = UserProfileEntity(
            alias = defaultAlias,
            avatarId = defaultAvatarId,
            createdAtEpochMillis = System.currentTimeMillis(),
            onboardingCompleted = false
        )
        val id = profileDao.insert(entity)
        progressDao.insert(ProgressEntity(userId = id))
        return entity.copy(id = id).toDomain()
    }

    suspend fun updateAlias(userId: Long, alias: String, avatarId: Int) {
        val current = profileDao.getProfileOnce() ?: return
        profileDao.update(current.copy(id = userId, alias = alias, avatarId = avatarId))
    }

    suspend fun completeOnboarding(userId: Long) {
        val current = profileDao.getProfileOnce() ?: return
        profileDao.update(current.copy(onboardingCompleted = true))
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        val current = profileDao.getProfileOnce() ?: return
        profileDao.update(current.copy(soundEnabled = enabled))
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        val current = profileDao.getProfileOnce() ?: return
        profileDao.update(current.copy(hapticsEnabled = enabled))
    }

    fun observeProgress(userId: Long): Flow<Progress?> = progressDao.observeProgress(userId).map { it?.toDomain() }

    suspend fun getProgressOnce(userId: Long): Progress =
        progressDao.getProgressOnce(userId)?.toDomain() ?: Progress(userId, 0, 1, 0, 0, 0)

    suspend fun saveProgress(progress: Progress) {
        progressDao.update(progress.toEntity())
    }

    fun observeAllBadges(): Flow<List<Badge>> = badgeDao.observeAllBadges().map { list -> list.map { it.toDomain() } }

    fun observeUnlockedBadges(userId: Long): Flow<List<UserBadge>> =
        badgeDao.observeUnlocked(userId).map { list -> list.map { UserBadge(it.badgeId, it.unlockedAtEpochMillis) } }

    suspend fun getUnlockedBadgeIds(userId: Long): Set<Long> = badgeDao.getUnlockedIds(userId).toSet()

    suspend fun unlockBadge(userId: Long, badgeId: Long) {
        badgeDao.unlock(UserBadgeEntity(userId, badgeId, System.currentTimeMillis()))
    }
}

private fun UserProfileEntity.toDomain() = UserProfile(id, alias, avatarId, createdAtEpochMillis, onboardingCompleted, soundEnabled, hapticsEnabled)
private fun ProgressEntity.toDomain() = Progress(userId, totalXp, level, casesCompleted, exercisesCompleted, exercisesCorrectFirstTry)
private fun Progress.toEntity() = ProgressEntity(userId, totalXp, level, casesCompleted, exercisesCompleted, exercisesCorrectFirstTry)
private fun com.educalab.staticdata.data.local.entity.BadgeEntity.toDomain() = Badge(id, code, title, description, iconKey, requirement)

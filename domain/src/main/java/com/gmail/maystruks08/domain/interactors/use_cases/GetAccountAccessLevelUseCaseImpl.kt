package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.entities.account.AssesLevel
import com.gmail.maystruks08.domain.repository.SettingsRepository
import javax.inject.Inject

class GetAccountAccessLevelUseCaseImpl @Inject constructor(
    private val repository: SettingsRepository
) : GetAccountAccessLevelUseCase {

    override suspend fun invoke(): AssesLevel {
        val currentUserId = repository.getCurrentUserId()
        val isCurrentUserAdmin = repository.getAdminUserIds().contains(currentUserId)
        return if (isCurrentUserAdmin) AssesLevel.Admin else AssesLevel.User
    }

}
package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.repository.EnvironmentUtil
import com.gmail.maystruks08.domain.repository.SettingsRepository
import javax.inject.Inject

class CheckIsValidAppVersionUseCaseImpl @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val environmentUtil: EnvironmentUtil
) : CheckIsValidAppVersionUseCase {

    override suspend fun invoke(): Boolean {
        val currentAppVersion = environmentUtil.getCurrentAppVersion()
        val actualAppVersions = settingsRepository.getActualAppVersion()
        if (actualAppVersions.isEmpty()) return true
        return actualAppVersions.contains(currentAppVersion)
    }

}
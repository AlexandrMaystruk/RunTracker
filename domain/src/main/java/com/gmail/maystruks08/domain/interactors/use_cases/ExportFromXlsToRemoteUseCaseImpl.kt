package com.gmail.maystruks08.domain.interactors.use_cases

import com.gmail.maystruks08.domain.interactors.use_cases.ExportFromXlsToRemoteUseCase
import com.gmail.maystruks08.domain.repository.SettingsRepository
import javax.inject.Inject

class ExportFromXlsToRemoteUseCaseImpl @Inject constructor(
    private val repository: SettingsRepository
) : ExportFromXlsToRemoteUseCase {

    override suspend fun invoke() {
        repository.exportRunnerDataFromXlsToRemote()
    }

}
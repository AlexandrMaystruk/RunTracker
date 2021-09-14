package com.gmail.maystruks08.nfcruntracker.ui.login

import com.gmail.maystruks08.data.awaitTaskCompletable
import com.gmail.maystruks08.domain.interactors.use_cases.LogOutUseCase
import com.gmail.maystruks08.domain.repository.SettingsRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LogOutUseCaseImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
    private val settingsRepository: SettingsRepository
) : LogOutUseCase {

    override suspend fun revokeAccess(): Boolean {
        return try {
            auth.signOut()
            awaitTaskCompletable(googleSignInClient.revokeAccess())
            settingsRepository.clearCurrentSelectedRace()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun logout(): Boolean {
        return try {
            auth.signOut()
            awaitTaskCompletable(googleSignInClient.signOut())
            settingsRepository.clearCurrentSelectedRace()
            true
        } catch (e: Exception) {
            false
        }
    }
}
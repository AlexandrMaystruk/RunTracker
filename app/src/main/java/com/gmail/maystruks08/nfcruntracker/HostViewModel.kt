package com.gmail.maystruks08.nfcruntracker

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.data.repository.RunnerDataChangeListener
import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.interactors.use_cases.CheckIsValidAppVersionUseCase
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class HostViewModel @ViewModelInject constructor(
    firebaseAuth: FirebaseAuth,
    private val router: Router,
    private val syncDataScheduler: SyncRunnersDataScheduler,
    private val runnerDataChangeListener: RunnerDataChangeListener,
    private val checkIsValidAppVersionUseCase: CheckIsValidAppVersionUseCase
) : BaseViewModel() {

    val applicationVersionInvalid = MutableStateFlow<Boolean?>(null)

    init {
        if (firebaseAuth.currentUser == null) {
            router.newRootScreen(Screens.LoginScreen())
        } else {
            viewModelScope.launch {
                val isVersionActual = checkIsValidAppVersionUseCase.invoke()
                if (!isVersionActual) applicationVersionInvalid.value = true
            }
            viewModelScope.launch {
                when (val result = runnerDataChangeListener.getLastSavedRace()) {
                    is TaskResult.Value -> router.newRootScreen(
                        Screens.MainScreen(
                            raceId = result.value.first,
                            raceName = result.value.second
                        )
                    )
                    is TaskResult.Error -> router.newRootScreen(Screens.RaceListScreen())
                }
            }
            syncDataScheduler.startSyncData(15, TimeUnit.MINUTES)
        }
    }

    fun exit(){
        router.exit()
    }

    override fun onCleared() {
        syncDataScheduler.stopAllWork()
        super.onCleared()
    }
}
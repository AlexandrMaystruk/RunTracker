package com.gmail.maystruks08.nfcruntracker

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.data.repository.RunnerDataChangeListener
import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import com.gmail.maystruks08.domain.entities.Change
import com.gmail.maystruks08.domain.entities.TaskResult
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.concurrent.TimeUnit

@ObsoleteCoroutinesApi
class HostViewModel @ViewModelInject constructor(
    firebaseAuth: FirebaseAuth,
    private val router: Router,
    private val syncDataScheduler: SyncRunnersDataScheduler,
    private val runnerDataChangeListener: RunnerDataChangeListener
) : BaseViewModel() {

    val runnerChange get(): LiveData<Change<Runner>> = runnerChangeLiveData
    private val runnerChangeLiveData = MutableLiveData<Change<Runner>>()

    init {
        if (firebaseAuth.currentUser == null) router.newRootScreen(Screens.LoginScreen())
        else {
            viewModelScope.launch {
                when (val result = runnerDataChangeListener.getLastSavedRaceId()) {
                    is TaskResult.Value -> router.newRootScreen(
                        Screens.RunnersScreen(result.value, null)
                    )
                    is TaskResult.Error -> router.newRootScreen(Screens.RaceListScreen())
                }
            }
        }
        syncDataScheduler.startSyncData(15, TimeUnit.MINUTES)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                runnerDataChangeListener
                    .observeRunnerData()
                    .collect { runnerChangeLiveData.postValue(it) }
            } catch (e: Exception) {
                Timber.e(e)
            }
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
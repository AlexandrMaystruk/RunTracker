package com.gmail.maystruks08.nfcruntracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.maystruks08.data.repository.RunnerDataChangeListener
import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import com.gmail.maystruks08.domain.entities.RunnerChange
import com.gmail.maystruks08.nfcruntracker.core.base.BaseViewModel
import com.gmail.maystruks08.nfcruntracker.core.navigation.Screens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HostViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val router: Router,
    private val syncDataScheduler: SyncRunnersDataScheduler,
    private val runnerDataChangeListener: RunnerDataChangeListener
) : BaseViewModel() {

    val runnerChange get(): LiveData<RunnerChange> = runnerChangeLiveData
    private val runnerChangeLiveData = MutableLiveData<RunnerChange>()

    init {
        if (firebaseAuth.currentUser == null) {
            router.newRootScreen(Screens.LoginScreen())
        } else {
            router.newRootScreen(Screens.RunnersScreen(0))
        }
        syncDataScheduler.startSyncData(15, TimeUnit.MINUTES)
        viewModelScope.launch(Dispatchers.IO) {
            runnerDataChangeListener.observeRunnerData()
                .catch { Timber.e(it) }
                .collect { runnerChangeLiveData.postValue(it) }
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